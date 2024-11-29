package hu.davidder.translations.translation.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import hu.davidder.translations.core.interceptors.MarketInterceptor;
import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.service.ImageService;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.TranslationImageInsertBody;
import hu.davidder.translations.translation.entity.TranslationTextInsertBody;
import hu.davidder.translations.translation.entity.Type;
import hu.davidder.translations.translation.repository.TranslationRepository;

/**
 * The TranslationService class provides methods for managing translation entities,
 * including creating, updating, deleting, forwarding, and converting translations.
 */
@Service
@CacheConfig(cacheNames = "translations")
@PropertySource("classpath:image-endpoint.properties")
public class TranslationService {

    @Lazy
    @Autowired
    private TranslationRepository repository;

    @Lazy
    @Autowired
    private ImageService imageService;

    @Value("${image.base.url.prefix}")
    private String imageUrlPrefix;

    @Value("${image.base.url.postfix}")
    private String imageUrlPostfix;

    /**
     * Retrieves all translations and replaces links for images.
     * 
     * @return An iterable collection of all translations.
     */
    @Cacheable(value = "translations", keyGenerator = "marketKeyGenerator", unless = "#result == null or #result.size()==0")
    public Iterable<Translation> findAll() {
        return replaceLinks(repository.findAll());
    }

    /**
     * Retrieves a translation by its key.
     * 
     * @param key The key of the translation.
     * @return The Translation entity that matches the given key.
     * @deprecated Use findById method instead.
     */
    @Deprecated
    public Translation findByKey(String key) {
        return replaceLink(repository.findByKey(key));
    }

    /**
     * Retrieves a translation by its ID.
     * 
     * @param id The ID of the translation.
     * @return The Translation entity that matches the given ID.
     */
    @Cacheable(value = "translation", key = "{#id}", unless = "#result == null")
    public Translation findById(long id) {
        return repository.findById(id).map(this::replaceLink).orElse(null);
    }

    /**
     * Retrieves a translation by its ID and type.
     * 
     * @param id The ID of the translation.
     * @param type The type of the translation.
     * @return The Translation entity that matches the given ID and type.
     */
    public Translation findByIdAndType(long id, Type type) {
        return replaceLink(repository.findByIdAndType(id, type));
    }

    /**
     * Sends key change events asynchronously.
     * 
     * @param emitter The SseEmitter to send events to.
     * @param key The key of the translation.
     */
    @Async("SSE")
    public void sendKeyChangeEvents(SseEmitter emitter, String key) {
        try {
            for (int i = 0; i < 10; i++) {
                emitter.send(repository.findByKey(key));
                emitter.send("Changed " + System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(2);
            }
            emitter.complete();
        } catch (IOException | InterruptedException e) {
            emitter.completeWithError(e);
        }
    }

    /**
     * Replaces links for images in the given translations.
     * 
     * @param data An iterable collection of translations.
     * @return An iterable collection of translations with replaced links.
     */
    public Iterable<Translation> replaceLinks(Iterable<Translation> data) {
        List<Translation> res = new LinkedList<>();
        data.forEach(d -> res.add(replaceLink(d)));
        return res;
    }

    /**
     * Replaces the link for an image in the given translation.
     * 
     * @param d The translation entity.
     * @return The translation entity with replaced link.
     */
    public Translation replaceLink(Translation d) {
        if (d.getType().equals(Type.IMAGE)) {
            d.setValue(imageUrlPrefix + MarketInterceptor.currentTenant.get() + "/" + imageUrlPostfix + d.getValue());
        }
        return d;
    }

    /**
     * Retrieves the translation repository.
     * 
     * @return The TranslationRepository instance.
     * @deprecated Directly use repository instead.
     */
    @Deprecated
    public TranslationRepository getRepository() {
        return repository;
    }

    /**
     * Creates a new text translation.
     * 
     * @param translationInsertBody The details of the translation to insert.
     * @return The created Translation entity.
     */
    public Translation create(TranslationTextInsertBody translationInsertBody) {
        Translation translation = new Translation(translationInsertBody.getKey(), translationInsertBody.getValue(), Type.TEXT);
        repository.save(translation);
        return translation;
    }

    /**
     * Forwards a translation to another translation.
     * 
     * @param originalId The ID of the original translation.
     * @param newId The ID of the new translation.
     * @return The updated Translation entity.
     */
    public Translation forward(long originalId, long newId) {
        Translation translation = findById(originalId);
        translation.setForwarded(findById(newId));
        translation.setModifyDate(ZonedDateTime.now());
        repository.save(translation);
        return translation;
    }

    /**
     * Disables forwarding for a translation.
     * 
     * @param id The ID of the translation.
     * @return The updated Translation entity.
     */
    public Translation disableForward(long id) {
        Translation translation = findById(id);
        translation.setForwarded(null);
        translation.setModifyDate(ZonedDateTime.now());
        repository.save(translation);
        return translation;
    }

    /**
     * Deletes a translation.
     * 
     * @param id The ID of the translation.
     * @return The updated Translation entity.
     */
    public Translation delete(long id) {
        Translation translation = findById(id);
        translation.setDeleted(true);
        repository.save(translation);
        return translation;
    }

    /**
     * Undeletes a translation.
     * 
     * @param id The ID of the translation.
     * @return The updated Translation entity.
     */
    public Translation undelete(long id) {
        Translation translation = findById(id);
        translation.setDeleted(false);
        repository.save(translation);
        return translation;
    }

    /**
     * Changes the status of a translation.
     * 
     * @param id The ID of the translation.
     * @param status The new status to set.
     * @return The updated Translation entity.
     */
    public Translation changeStatus(long id, boolean status) {
        Translation translation = findById(id);
        translation.setStatus(status);
        repository.save(translation);
        return translation;
    }

    /**
     * Creates a new image translation.
     * 
     * @param translationInsertBody The details of the image translation to insert.
     * @return The created Translation entity.
     */
    public Translation createImage(TranslationImageInsertBody translationInsertBody) {
        Translation newTranslation = new Translation(translationInsertBody.getKey(), "", Type.IMAGE);
        List<Image> images = imageService.createImages(translationInsertBody.getValue(), newTranslation);
        newTranslation.setImages(images);
        newTranslation.setValue(images.get(0).getName());
        repository.save(newTranslation);
        return replaceLink(newTranslation);
    }

    /**
     * Converts translations to a format suitable for Angular.
     * 
     * @param data An iterable collection of translations.
     * @return A map of translation keys and values.
     */
    public Map<String, String> convertToAngular(Iterable<Translation> data) {
        Map<String, String> res = new HashMap<>();
        data.forEach(curr -> res.put(curr.getKey(), curr.getValue()));
        return res;
    }
}
