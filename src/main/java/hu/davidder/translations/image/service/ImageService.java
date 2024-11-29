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
import org.springframework.stereotype.Service;

import hu.davidder.translations.image.entity.Image;
import hu.davidder.translations.image.entity.ImageInsertBody;
import hu.davidder.translations.image.entity.ImageType;
import hu.davidder.translations.image.repository.ImageRepository;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.repository.TranslationRepository;

/**
 * The ImageService class provides methods to manage image entities including
 * resizing images, retrieving images from the CDN, and creating new image entries.
 */
@Service
@CacheConfig(cacheNames = "images")
public class ImageService {

    @Lazy
    @Autowired
    private ImageRepository imageRepository;

    @Lazy
    @Autowired
    private TranslationRepository repository;

    /**
     * Retrieves an image from the CDN based on the given name and target size.
     * 
     * @param name The name of the image.
     * @param targetSize The target size of the image.
     * @return The Image entity matching the name and target size.
     */
    @Cacheable(value = "image", keyGenerator = "imageKeyGenerator", unless = "#result == null")
    public Image getCdnImage(String name, Integer targetSize) {
        return imageRepository.getUrl(name, targetSize);
    }

    /**
     * Retrieves the image as a byte array without resizing.
     * 
     * @param url The URL of the image.
     * @return The byte array of the image.
     */
    public byte[] getImage(String url) {
        return resizeImage(url, 0, false);
    }

    /**
     * Resizes the image from the given URL to the specified resolution.
     * 
     * @param url The URL of the image.
     * @param res The resolution to resize the image to.
     * @return The byte array of the resized image.
     */
    public byte[] resizeImage(String url, int res) {
        return resizeImage(url, res, true);
    }

    /**
     * Helper method to resize the image with or without the specified resolution.
     * 
     * @param url The URL of the image.
     * @param res The resolution to resize the image to.
     * @param withResize Whether to resize the image.
     * @return The byte array of the resized image.
     */
    public byte[] resizeImage(String url, int res, boolean withResize) {
        if (res == 0) {
            withResize = false;
        }
        try {
            BufferedImage image = ImageIO.read(new URI(url).toURL());
            if (withResize && image != null) {
                image = Scalr.resize(image, res);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String type = url.contains(ImageType.JPG.value) ? ImageType.JPG.typeName : ImageType.PNG.typeName;
            if (image == null) {
                return new byte[0];
            }
            ImageIO.write(image, type, baos);
            return baos.toByteArray();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Retrieves the image repository.
     * 
     * @return The ImageRepository instance.
     */
    public ImageRepository getImageRepository() {
        return imageRepository;
    }

    /**
     * Creates images for a given Translation based on the ImageInsertBody details.
     * 
     * @param imageInsertBody The details of the image to insert.
     * @param translation The Translation entity to associate the images with.
     * @return The list of created Image entities.
     */
    public List<Image> createImages(ImageInsertBody imageInsertBody, Translation translation) {
        return createImages(imageInsertBody.getUrl(), imageInsertBody.getTargetSizes(), translation);
    }

    /**
     * Creates images for a given Translation based on the provided URL and target sizes.
     * 
     * @param url The URL of the image.
     * @param sizes The list of target sizes for the image.
     * @param translation The Translation entity to associate the images with.
     * @return The list of created Image entities.
     */
    public List<Image> createImages(String url, List<Integer> sizes, Translation translation) {
        String name = UUID.randomUUID().toString();
        ImageType type = url.contains(ImageType.JPG.value) ? ImageType.JPG : ImageType.PNG;
        if (sizes.isEmpty() || !sizes.contains(0)) {
            sizes.add(0);
        }
        List<Image> images = new LinkedList<>();
        for (int targetSize : sizes) {
            Image image = new Image();
            image.setTargetSize(targetSize);
            image.setValue(resizeImage(url, targetSize));
            image.setName(name);
            image.setType(type);
            images.add(image);
            if (targetSize == 0) {
                translation.setValue(name);
            }
        }
        translation.setImages(images);
        repository.save(translation);
        return images;
    }
}
