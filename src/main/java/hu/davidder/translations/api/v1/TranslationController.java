package hu.davidder.translations.api.v1;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import hu.davidder.translations.image.service.ImageService;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.TranslationImageInsertBody;
import hu.davidder.translations.translation.entity.TranslationTextInsertBody;
import hu.davidder.translations.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * The TranslationController class handles API requests related to translations,
 * including creating, updating, deleting, and streaming translations.
 */
@RestController
@PropertySource("classpath:translation-endpoint.properties")
@RequestMapping("${base.endpoint}")
@Tag(name = "Translation", description = "Endpoints for querying translations")
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TranslationController {

    @Lazy
    @Autowired
    private TranslationService translationService;

    @Lazy
    @Autowired
    private ImageService imageService;

    /**
     * Retrieves all translations.
     *
     * @return All translations.
     */
    @Deprecated
    @GetMapping(value = "${find.all.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all translations", description = "This will query every translation",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Iterable.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Iterable<Translation>> getAllTranslations() {
        try {
            return ResponseEntity.ok(translationService.findAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves all translations formatted for Angular.
     *
     * @return A map of translations formatted for Angular.
     */
    @GetMapping(value = "/{market}/translations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all translations", description = "This will query every translation",
            parameters = @Parameter(in = ParameterIn.PATH, name = "market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Map<String, String>> getAllTranslationsForAngular() {
        try {
            return ResponseEntity.ok(translationService.convertToAngular(translationService.findAll()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Retrieves a translation by key.
     *
     * @param key The key of the translation.
     * @return The translation.
     */
    @GetMapping(value = "/{market}/${find.by.key.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get translation by key", description = "This will query translation with a specific key",
            parameters = @Parameter(in = ParameterIn.PATH, name = "market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> getTranslation(@PathVariable(name="key") String key) {
        return ResponseEntity.ok(translationService.findByKey(key));
    }

    /**
     * Retrieves a translation by key (deprecated method).
     *
     * @param key The key of the translation.
     * @return The translation.
     */
    @Deprecated
    @GetMapping(value = "${find.by.key.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get translation by key", description = "This will query translation with a specific key",
            parameters = @Parameter(in = ParameterIn.PATH, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> getTranslations(@PathVariable(name="key") String key) {
        return getTranslation(key);
    }

    /**
     * Streams translation changes.
     *
     * @return The SseEmitter.
     */
    @Deprecated
    @GetMapping(value = "/stream/change", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream", description = "Transaction change stream")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streaming", content = @Content(schema = @Schema(implementation = SseEmitter.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public SseEmitter change() {
        SseEmitter emitter = new SseEmitter(10000L);
        translationService.sendKeyChangeEvents(emitter, "translation-1-text");
        return emitter;
    }

    /**
     * Creates a translation of text type.
     *
     * @param translationInsertBody The details of the translation to insert.
     * @return The created translation.
     */
    @PostMapping("${create.text}")
    @Operation(summary = "Create translation - Text type", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> createText(@RequestBody TranslationTextInsertBody translationInsertBody) {
        return ResponseEntity.ok(translationService.create(translationInsertBody));
    }

    /**
     * Forwards a translation.
     *
     * @param originalId The original ID of the translation.
     * @param newId The new ID to forward to.
     * @return The forwarded translation.
     */
    @PatchMapping("forward/{originalId}/{newId}")
    @Operation(summary = "Forward translation", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> forward(@PathVariable(name="originalId") long originalId, @PathVariable(name="newId") long newId) {
        return ResponseEntity.ok(translationService.forward(originalId, newId));
    }

    /**
     * Disables forwarding for a translation.
     *
     * @param id The ID of the translation.
     * @return The translation with forwarding disabled.
     */
    @PatchMapping("forward/disable/{id}")
    @Operation(summary = "Disable translation forwarding", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> disableForward(@PathVariable(name="id") long id) {
        return ResponseEntity.ok(translationService.disableForward(id));
    }

    /**
     * Deletes a translation (not physically).
     *
     * @param id The ID of the translation.
     * @return The deleted translation.
     */
    @DeleteMapping("delete/{id}")
    @Operation(summary = "Delete translation - (not physical)", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> delete(@PathVariable(name="id") long id) {
        Translation translation = translationService.delete(id);
        return ResponseEntity.ok(translation);
    }

    /**
     * Undeletes a translation.
     *
     * @param id The ID of the translation.
     * @return The undeleted translation.
     */
    @PatchMapping("undelete/{id}")
    @Operation(summary = "Undelete translation", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> undelete(@PathVariable(name="id") long id) {
        Translation translation = translationService.undelete(id);
        return ResponseEntity.ok(translation);
    }

    /**
     * Changes the status of a translation.
     *
     * @param id The ID of the translation.
     * @param status The new status of the translation (true for enabled, false for disabled).
     * @return The updated translation.
     */
    @PatchMapping("status/{id}/{status}")
    @Operation(summary = "Change translation status - Enabled/Disabled (true/false)", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> changeStatus(@PathVariable(name="id") long id, @PathVariable(name="status") boolean status) {
        Translation translation = translationService.changeStatus(id, status);
        return ResponseEntity.ok(translation);
    }

    /**
     * Creates a translation of image type.
     *
     * @param translationInsertBody The details of the translation to insert.
     * @return The created translation.
     */
    @PostMapping("${create.image}")
    @Operation(summary = "Create translation - Image type", description = "TBC",
            parameters = @Parameter(in = ParameterIn.HEADER, name = "X-Market", description = "Custom market. Example: en-th", schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
            @ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Translation> create(@RequestBody TranslationImageInsertBody translationInsertBody) {
        Translation translation = translationService.createImage(translationInsertBody);
        return ResponseEntity.ok(translation);
    }
}


