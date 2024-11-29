package hu.davidder.translations.translation.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import hu.davidder.translations.translation.entity.Translation;

/**
 * TranslationRepository is a repository interface for Translation entities.
 * It extends CrudRepository to provide basic CRUD operations
 * and CustomTranslationRepository for custom repository operations.
 */
@Repository
public interface TranslationRepository extends CrudRepository<Translation, Long>, CustomTranslationRepository {
    // Additional custom methods can be defined here if needed
}
