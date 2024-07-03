package hu.davidder.translation.api.image.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.translation.api.image.entity.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long>, CustomImageRepository{
	
}
