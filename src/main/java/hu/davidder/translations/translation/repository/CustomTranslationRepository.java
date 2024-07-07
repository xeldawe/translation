package hu.davidder.translations.translation.repository;

import hu.davidder.translations.translation.entity.Translation;

public interface  CustomTranslationRepository  {
	Translation findByKey(String key);
}
