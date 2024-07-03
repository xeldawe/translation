package hu.davidder.translation.api.translation.controller.v1;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import hu.davidder.translation.api.image.entity.Image;
import hu.davidder.translation.api.image.entity.ImageType;
import hu.davidder.translation.api.image.service.ImageService;
import hu.davidder.translation.api.translation.entity.Translation;
import hu.davidder.translation.api.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1")
@CrossOrigin()
@Tag(name = "Translation", description = "Endpoints for querying translations")
public class TranslationController {

	@Autowired
    private TranslationService translationService;
	
	@Autowired
	private ImageService imageService;
    
	@GetMapping("/translations")
	@Operation(summary = "Get all translations", description = "This will query every translations")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Everything is fine",
					content = @Content(schema = @Schema(implementation = Iterable.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public ResponseEntity<Iterable<Translation>> getAllTranslations(){
		return ResponseEntity
				.ok()
				.body(translationService.findAll()); 
	}
	
	
	@GetMapping("/translation/{key}") 
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
	
	@GetMapping("init")
	public void init() {
		try {
			translationService.initDummyData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	 
	@GetMapping("images/{name}")
	@Operation(summary = "Return image from databse", description = "TBC")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Everything is fine",
					content = @Content(schema = @Schema(implementation = Byte[].class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(",
			content = @Content(schema = @Schema(implementation = Void.class))),
	})
	public ResponseEntity<byte[]> getImage(@PathVariable("name") String name, @RequestParam(defaultValue = "0") Integer targetSize){
        Image img = imageService.getCdnImage(name, targetSize);
        byte[] response = img.getValue();
        return ResponseEntity
        		.ok()
        		.contentType(img.getType().equals(ImageType.PNG)?MediaType.IMAGE_PNG:MediaType.IMAGE_JPEG)
        		.body(response);
    }
	
	
}

