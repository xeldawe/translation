package hu.davidder.translations.image.repository;

import hu.davidder.translations.image.entity.Image;

public interface CustomImageRepository {
	Image getUrl(String name, Integer targetSize);
}
