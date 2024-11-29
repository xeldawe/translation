package hu.davidder.translations.api.v1;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
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

import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.service.ImageService;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.TranslationImageInsertBody;
import hu.davidder.translations.translation.entity.TranslationTextInsertBody;
import hu.davidder.translations.translation.entity.Type;
import hu.davidder.translations.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
	
    
	@Deprecated
	@GetMapping(value = "${find.all.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all translations", description = "This will query every translations",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Everything is fine",
					content = @Content(schema = @Schema(implementation = Iterable.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public ResponseEntity<Iterable<Translation>> getAllTranslations(){
	
		try {
	        return ResponseEntity
			.ok()
			.body(translationService.findAll()); 
		}catch (Exception e) {
			return ResponseEntity
					.internalServerError()
					.build(); 
		}
	}
	
	@Operation(summary = "Get all translations", description = "This will query every translations",
			parameters = 
			@Parameter(
				in = ParameterIn.PATH,
				name = "market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Everything is fine",
					content = @Content(schema = @Schema(implementation = Iterable.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	@GetMapping(value = "/{market}/translations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String,String>> getAlLTranslationsForAngular(){
		Map<String, String> res  = translationService.convertToAngular(translationService.findAll());
		try {
	        return ResponseEntity
			.ok()
			.body(res); 
		}catch (Exception e) {
			return ResponseEntity
					.internalServerError()
					.build(); 
		}
	}
	
	@GetMapping(value = "/{market}/${find.by.key.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE) 
	@Operation(summary = "Get translation by key", description = "This will query translation with a specific key",
			parameters = 
			@Parameter(
				in = ParameterIn.PATH,
				name = "market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Everything is fine",
					content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public ResponseEntity<Translation> getTranslationsV2(@PathVariable String key) {
		return  ResponseEntity
				.ok()
				.body(translationService.findByKey(key));
	}
	
	@Deprecated
	@GetMapping(value = "${find.by.key.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE) 
	@Operation(summary = "Get translation by key", description = "This will query translation with a specific key",
			parameters = 
			@Parameter(
				in = ParameterIn.PATH,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Everything is fine",
					content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public ResponseEntity<Translation> getTranslations(@PathVariable String key) {
		return  ResponseEntity
				.ok()
				.body(translationService.findByKey(key));
	}

	
	//TODO fix this dummy 
	@Deprecated
	@GetMapping(value = "/stream/change", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "Stream", description = "Transaction change stream")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Streaming",
					content = @Content(schema = @Schema(implementation = SseEmitter.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public SseEmitter change() {
	    SseEmitter emitter = new SseEmitter(10000l);
	    translationService.sendKeyChangeEvents(emitter, "translation-1-text");
		return emitter;
	}
	
	//TODO HANDLE ERRORS
	@PostMapping("${create.text}")
	@Operation(summary = "Create translation - Text type", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> create(@RequestBody TranslationTextInsertBody translationInsertBody) {
		Translation translation = translationService.create(translationInsertBody);
		return ResponseEntity.ok(translation);
	}
	
	//TODO Config
	@PatchMapping("forward/{originalId}/{newId}")
	@Operation(summary = "Forward translation", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> forward(@PathVariable long originalId, @PathVariable long newId) {
		Translation translation = translationService.forward(originalId,newId);
		return ResponseEntity.ok(translation);
	}
	
	//TODO Config
	@PatchMapping("forward/disable/{id}")
	@Operation(summary = "Disable translation forwarding", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> disableForward(@PathVariable long id) {
		Translation translation = translationService.disableForward(id);
		return ResponseEntity.ok(translation);
	}
	
	//TODO Config
	/**
	 * This is not a physical delete
	 * @param id
	 * @return
	 */
	@DeleteMapping("delete/{id}")
	@Operation(summary = "Delete translation - (not physical)", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> delete(@PathVariable long id) {
		Translation translation = translationService.delete(id);
		return ResponseEntity.ok(translation);
	}
	
	//TODO Config
	@PatchMapping("undelete/{id}")
	@Operation(summary = "Undelete translation", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> undelete(@PathVariable long id) {
		Translation translation = translationService.undelete(id);
		return ResponseEntity.ok(translation);
	}

	
	//TODO Config
	@PatchMapping("status/{id}/{status}")
	@Operation(summary = "Change translation status - Enabled/Disabled (true/false)", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> changeStatus(@PathVariable long id, @PathVariable boolean status) {
		Translation translation = translationService.changeStatus(id,status);
		return ResponseEntity.ok(translation);
	}



	//TODO HANDLE ERRORS
	@PostMapping("${create.image}")
	@Operation(summary = "Create translation - Image type", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Translation.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<Translation> create(@RequestBody TranslationImageInsertBody translationInsertBody) {
		Translation translation = translationService.createImage(translationInsertBody);
		return ResponseEntity.ok(translation);
	}
}

