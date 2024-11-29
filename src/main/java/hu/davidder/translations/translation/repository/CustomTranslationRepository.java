package hu.davidder.translations.translation.repository;

import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.Type;

/**
 * CustomTranslationRepository is an interface for custom repository operations on Translation entities.
 */
public interface CustomTranslationRepository {

    /**
     * Finds a translation by its key.
     * 
     * @param key The key of the translation.
     * @return The Translation entity that matches the given key.
     */
    Translation findByKey(String key);

    /**
     * Finds a translation by its ID and type.
     * 
     * @param id The ID of the translation.
     * @param type The type of the translation.
     * @return The Translation entity that matches the given ID and type.
     */
    Translation findByIdAndType(long id, Type type);
}
