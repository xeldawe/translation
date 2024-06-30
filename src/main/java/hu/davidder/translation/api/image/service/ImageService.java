package hu.davidder.translation.api.image.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.davidder.translation.api.image.entity.Image;
import hu.davidder.translation.api.image.repository.ImageRepository;

@Service
@CacheConfig
public class ImageService {
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Cacheable(value = "image", key = "{#name, #targetSize}")
	public Image getCdnImage(String name, Integer targetSize) throws JsonProcessingException {
		return imageRepository.getUrl(name, targetSize);
	}
	
	public byte[] getImage(String url) {
		return resizeImage(url, 0, false);
	}

	public byte[] resizeImage(String url, int res) {
		return resizeImage(url, res, true);
	}

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