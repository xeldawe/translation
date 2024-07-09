package hu.davidder.translations.translation.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.entity.ImageType;
import hu.davidder.translations.image.service.ImageService;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.Type;
import hu.davidder.translations.translation.repository.TranslationRepository;
import jakarta.persistence.Transient;

@Service
@CacheConfig
@PropertySource("classpath:image-endpoint.properties")
public class TranslationService {

	@Lazy
	@Autowired
	private TranslationRepository repository;

	@Lazy
	@Autowired
	private ImageService imageService;

	@Value("${image.base.url}")
	private String imageUrlPrefix;

	@Deprecated
	public void loadFromFile(String path) throws JsonMappingException, JsonProcessingException, IOException {
		Map<String, String> result = new ObjectMapper().readValue(
				FileUtils.readFileToString(new File(path),
						StandardCharsets.UTF_8),
				HashMap.class);
		List<Translation> data = new LinkedList<>();
		int[] counter = new int[1];
		counter[0] = 0;
		result.forEach((k, v) -> {
			System.out.println("Current - " + (++counter[0]) + "/" + result.size());
			Translation t = new Translation();
			t.setKey(k);
			if (v.startsWith("http")) {
				t.setType(Type.IMAGE);
				ImageType type = ImageType.PNG;
				if (v.contains(ImageType.JPG.value)) {
					type = ImageType.JPG;
				}
				String name = UUID.randomUUID().toString();
				Image image = new Image();
				image.setTargetSize(0);
				image.setTranslation(t);
				image.setValue(imageService.resizeImage(v, 128));
				image.setName(name);
				image.setType(type);
				List<Image> imgs = new ArrayList<>();
				imgs.add(image);
				t.setImages(imgs);
				t.setValue(name);
			} else {
				t.setType(Type.TEXT);
				t.setValue(v);
			}
			if (v.length() < 20000) // TODO
				data.add(t);
		});
		repository.saveAll(data);
	}

	@Cacheable(value = "translations", keyGenerator="marketKeyGenerator",unless="#result == null or #result.size()==0")
	public Iterable<Translation> findAll() {
		return replaceLinks(repository.findAll());
	}

	@Deprecated
	//@Cacheable(value = "translation", key = "{#key}",unless="#result == null")
	public Translation findByKey(String key) {
		return replaceLink(repository.findByKey(key));
	}

	public Translation findByIdAndType(long id, Type type) {
		return replaceLink(repository.findByIdAndType(id, type));
	}

	@Async("SSE")
	public void sendKeyChangeEvents(SseEmitter emitter, String key) {
		try {
			for (int i = 0; i < 10; i++) {
				emitter.send(repository.findByKey(key)); // TODO
				emitter.send("Changed " + System.currentTimeMillis());
				TimeUnit.SECONDS.sleep(2);
			}
			emitter.complete();
		} catch (IOException | InterruptedException e) {
			emitter.completeWithError(e);
		}
	}

	public Iterable<Translation> replaceLinks(Iterable<Translation> data) {
		List<Translation> res = new LinkedList<>();
		for (Translation d : data) {
			res.add(replaceLink(d));
		}
		return res;
	}
	
	public Translation replaceLink(Translation d) {
		if (d.getType().equals(Type.IMAGE)) {
			d.setValue(imageUrlPrefix + d.getValue());
		}
		return d;
	}

	public TranslationRepository getRepository() {
		return repository;
	}
	
	
}
