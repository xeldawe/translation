package hu.davidder.translation.api.image.repository;

import hu.davidder.translation.api.image.entity.Image;

public interface CustomImageRepository {
	Image getUrl(String name, Integer targetSize);
}
