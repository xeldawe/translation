package hu.davidder.translations.image.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.translations.image.entity.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long>, CustomImageRepository{
	
}
