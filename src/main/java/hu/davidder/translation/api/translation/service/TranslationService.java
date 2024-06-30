package hu.davidder.translation.api.translation.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.translation.api.image.entity.Image;
import hu.davidder.translation.api.image.service.ImageService;
import hu.davidder.translation.api.translation.entity.Translation;
import hu.davidder.translation.api.translation.entity.Type;
import hu.davidder.translation.api.translation.repository.TranslationRepository;

@Service
@CacheConfig
public class TranslationService {

	@Autowired
	private TranslationRepository repository;

	@Autowired
	private ImageService imageService;

	public void initDummyData() throws JsonMappingException, JsonProcessingException, IOException {
		Map<String, String> result = new ObjectMapper().readValue(
				FileUtils.readFileToString(new File("C:\\Users\\Xel\\Desktop\\Project\\test\\translation.json"),
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
				String type = "PNG";
				if (v.contains(".jpg")) {
					type = "JPG";
				}
				String name = "" + UUID.randomUUID();
				Image image2 = new Image();
				image2.setTargetSize(0);
				image2.setTranslation(t);
				image2.setValue(imageService.getImage(v));
				image2.setName(name);
				image2.setType(type.equals("PNG") ? "PNG" : "JPEG");
				Image image = new Image();
				image.setTargetSize(128);
				image.setTranslation(t);
				image.setValue(imageService.resizeImage(v, 128));
				image.setName(name);
				image.setType(type.equals("PNG") ? "PNG" : "JPEG");
				List<Image> imgs = new ArrayList<>();
				imgs.add(image2);
				imgs.add(image);
				t.setImages(imgs);
				t.setValue("http://localhost:8080/images/" + name);
			} else {
				t.setType(Type.TEXT);
				t.setValue(v);
			}
			if (v.length() < 20000) // TODO
				data.add(t);
		});
		repository.saveAll(data);
	}

	@Cacheable(value = "translations")
	public Iterable<Translation> findAll() {
		return repository.findAll();
	}

	@Cacheable(value = "translation", key = "#key")
	public Translation findByKey(String key) {
		return repository.findByKey(key);
	}
}
