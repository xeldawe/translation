package hu.davidder.translations.api.v1;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import hu.davidder.translations.core.interceptors.MarketInterceptor;
import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.entity.ImageInsertBody;
import hu.davidder.translations.image.entity.ImageType;
import hu.davidder.translations.image.service.ImageService;
import hu.davidder.translations.translation.entity.Translation;
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
import jakarta.persistence.NoResultException;

@RestController
@PropertySource("classpath:image-endpoint.properties")
@RequestMapping("${base.endpoint}")
@Tag(name = "Image", description = "Endpoints for querying images")
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ImageController {

	@Lazy
	@Autowired
	private ImageService imageService;

	@Lazy
	@Autowired
	private TranslationService translationService;

	@Value("${image.base.url.prefix}")
	private String imageUrlPrefix;
	@Value("${image.base.url.postfix}")
	private String imageUrlPostfix;

	@Deprecated
	@GetMapping(value = "${find.image.by.name.endpoint}", produces = { MediaType.IMAGE_PNG_VALUE,
			MediaType.IMAGE_JPEG_VALUE })
	@Operation(summary = "Return image from database", description = "TBC",
			parameters = 
			@Parameter(
				in = ParameterIn.HEADER,
				name = "X-Market",
				description = "Custom market. Example: en-th",
				required = false,
				schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Byte[].class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<byte[]> getImage(@PathVariable("name") String name,
			@RequestParam(name="targetSize", defaultValue = "0") int targetSize) {
		Image img = imageService.getCdnImage(name, targetSize);
		byte[] response = img.getValue();
		return ResponseEntity.ok()
				.contentType(img.getType().equals(ImageType.PNG) ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
				.body(response);
	}
	
	@GetMapping(value = "/{market}/${find.image.by.name.endpoint}", produces = { MediaType.IMAGE_PNG_VALUE,
			MediaType.IMAGE_JPEG_VALUE })
	@Operation(summary = "Return image from database", description = "TBC",
	parameters = 
	@Parameter(
		in = ParameterIn.PATH,
		name = "market",
		description = "Custom market. Example: en-th",
		required = false,
		schema = @Schema(type = "string")))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = Byte[].class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<byte[]> getImageV2(@PathVariable("name") String name,
			@RequestParam(name="targetSize", defaultValue = "0") int targetSize) {
		Image img = imageService.getCdnImage(name, targetSize);
		byte[] response = img.getValue();
		return ResponseEntity.ok()
				.contentType(img.getType().equals(ImageType.PNG) ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
				.body(response);
	}


	@PutMapping("${create}/{translationId}")
	@Operation(summary = "Adding or replacing image on specific IMAGE type translation", description = "TBC", 
	parameters = 
		@Parameter(
			in = ParameterIn.HEADER,
			name = "X-Market",
			description = "Custom market. Example: en-th",
			required = false,
			schema = @Schema(type = "string"))
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Everything is fine", content = @Content(schema = @Schema(implementation = List.class))),
			@ApiResponse(responseCode = "404", description = "Transaction not found or this is not IMAGE type", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "500", description = "Oh nooo.. :(", content = @Content(schema = @Schema(implementation = Void.class))), })
	public ResponseEntity<List<String>> saveImage(@PathVariable("translationId") Long translationId,
			@RequestBody ImageInsertBody imageInsertBody) {
		try {
			Translation translation = translationService.findByIdAndType(translationId, Type.IMAGE);
			translation.setModifyDate(ZonedDateTime.now());
			List<String> imageUrls = new LinkedList<>();
			try {
				Iterable<Image> res = imageService.createImages(imageInsertBody.getUrl(),imageInsertBody.getTargetSizes(),translation);
				imageService.getImageRepository().saveAll(res);
				for (Image img : res) {
					imageUrls.add(imageUrlPrefix+MarketInterceptor.currentTenant.get()+"/"+imageUrlPostfix+ img.getName()+"?targetSize="+img.getTargetSize());
				}
			} catch (Exception e) {
				ResponseEntity.internalServerError().build();
			}
			return ResponseEntity.ok().body(imageUrls);
		} catch (NoResultException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
