package hu.davidder.translations.image.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.entity.ImageInsertBody;
import hu.davidder.translations.image.entity.ImageType;
import hu.davidder.translations.image.repository.ImageRepository;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.TranslationImageInsertBody;

@Service
@CacheConfig
public class ImageService {
	
	@Lazy
	@Autowired
	private ImageRepository imageRepository;
	
	@Cacheable(value = "image", key = "{#name, #targetSize}", unless="#result == null")
	public Image getCdnImage(String name, Integer targetSize) {
		return imageRepository.getUrl(name, targetSize);
	}
	
	public byte[] getImage(String url) {
		return resizeImage(url, 0, false);
	}

	public byte[] resizeImage(String url, int res) {
		return resizeImage(url, res, true);
	}

	@Async
	byte[] resizeImage(String url, int res, boolean withResize) {
		try {
			BufferedImage image = ImageIO.read(new URI(url).toURL());
			if (withResize && image != null)
				image = Scalr.resize(image, res);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String type = ImageType.PNG.name;
			if (url.contains(ImageType.JPG.value)) {
				type = ImageType.JPG.name;
			}
			if (image == null)
				return null;
			ImageIO.write(image, type, baos);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ImageRepository getImageRepository() {
		return imageRepository;
	}
	
	public List<Image> createImages(ImageInsertBody imageInsertBody, Translation translation){
		return createImages(imageInsertBody.getUrl(), imageInsertBody.getTargetSizes(), translation);
	}
	
	public List<Image> createImages(String url, List<Integer> sizes, Translation translation){
		String name = UUID.randomUUID().toString();
		ImageType type = ImageType.PNG;
		if (url.contains(ImageType.JPG.value)) {
			type = ImageType.JPG;
		}
		if(sizes.isEmpty() || !sizes.contains(0)) {
			sizes.add(0);
		}
		List<Image> images = new LinkedList<>();
		for (int targetSieze : sizes) {
			Image image = new Image();
			image.setTargetSize(targetSieze);
			image.setTranslation(translation);
			image.setValue(resizeImage(url, targetSieze));
			image.setName(name);
			image.setType(type);
			images.add(image);
		}
		return images;
	}
	
}