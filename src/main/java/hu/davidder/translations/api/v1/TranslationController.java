package hu.davidder.translations.api.v1;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@PropertySource("classpath:translation-endpoint.properties")
@RequestMapping("${base.endpoint}")
@CrossOrigin()
@Tag(name = "Translation", description = "Endpoints for querying translations")
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TranslationController {
	
	@Lazy
	@Autowired
    private TranslationService translationService;
	
    
	@GetMapping(value = "${findAll.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all translations", description = "This will query every translations")
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
	
	
	@GetMapping(value = "${findByKey.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE) 
	@Operation(summary = "Get translation by key", description = "This will query translation with a specific key")
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
	
	@Deprecated
	@GetMapping("init")
	public void init() {
		try {
			translationService.initDummyData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	 
	
	//TODO fix this dummy 
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
	 

}

