package hu.davidder.translation.api.translation.repository;

import hu.davidder.translation.api.translation.entity.Translation;

public interface  CustomTranslationRepository  {
	Translation findByKey(String key);
}
