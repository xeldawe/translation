package hu.davidder.translations.image.repository;

import hu.davidder.translations.image.entity.Image;

/**
 * CustomImageRepository is an interface for custom repository operations on Image entities.
 */
public interface CustomImageRepository {

    /**
     * Retrieves an image based on the given name and target size.
     * 
     * @param name The name of the image.
     * @param targetSize The target size of the image.
     * @return The Image entity that matches the given name and target size.
     */
    Image getUrl(String name, Integer targetSize);
}
