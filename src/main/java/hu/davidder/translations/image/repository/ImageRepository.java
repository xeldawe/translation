package hu.davidder.translations.image.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import hu.davidder.translations.image.entity.Image;

/**
 * ImageRepository is a repository interface for Image entities.
 * It extends CrudRepository to provide basic CRUD operations
 * and CustomImageRepository for custom repository operations.
 */
@Repository
public interface ImageRepository extends CrudRepository<Image, Long>, CustomImageRepository {
    // Additional custom methods can be defined here if needed
}
