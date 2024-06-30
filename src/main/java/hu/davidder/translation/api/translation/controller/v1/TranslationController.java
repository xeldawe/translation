package hu.davidder.translation.api.translation.controller.v1;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import hu.davidder.translation.api.image.entity.Image;
import hu.davidder.translation.api.image.service.ImageService;
import hu.davidder.translation.api.translation.entity.Translation;
import hu.davidder.translation.api.translation.service.TranslationService;

@RestController
@RequestMapping("api/v1")
public class TranslationController {

	@Autowired
    private TranslationService translationService;
	
	@Autowired
	private ImageService imageService;
    
	@GetMapping("/translations")
	public ResponseEntity<Iterable<Translation>> getAllTranslations() throws JsonProcessingException {
		return ResponseEntity
				.ok()
				.body(translationService.findAll()); 
	}
	
	
	@GetMapping("/translation/{key}") 
	public ResponseEntity<Translation> getTranslations(@PathVariable String key) throws JsonMappingException, JsonProcessingException {
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
	public ResponseEntity<byte[]> getImage(@PathVariable("name") String name, @RequestParam(defaultValue = "0") Integer targetSize) throws JsonMappingException, JsonProcessingException {
        Image img = imageService.getCdnImage(name, targetSize);
        byte[] response = img.getValue();
        return ResponseEntity
        		.ok()
        		.contentType(
        				img
        				.getType()
        				.equals("PNG")
        						?
        						MediaType.IMAGE_PNG
        						:
        						MediaType.IMAGE_JPEG
        						)
        		.body(response);
    }
	
	
}

