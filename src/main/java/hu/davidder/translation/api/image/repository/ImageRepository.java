package hu.davidder.translation.api.image.repository;

import org.springframework.data.repository.CrudRepository;

import hu.davidder.translation.api.image.entity.Image;

public interface ImageRepository extends CrudRepository<Image, Long>, CustomImageRepository{
	
}
