package hu.davidder.translations.image.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.repository.ImageRepository;

@Service
@CacheConfig
public class ImageService {
	
	@Lazy
	@Autowired
	private ImageRepository imageRepository;
	
	@Cacheable(value = "image", key = "{#name, #targetSize}")
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
			String type = "PNG";
			if (url.contains(".jpg")) {
				type = "JPG";
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
}