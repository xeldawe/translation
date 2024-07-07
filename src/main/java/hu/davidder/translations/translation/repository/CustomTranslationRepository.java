package hu.davidder.translations.translation.repository;

import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.Type;

public interface  CustomTranslationRepository  {
	Translation findByKey(String key);
	Translation findByIdAndType(long id, Type type);
}
