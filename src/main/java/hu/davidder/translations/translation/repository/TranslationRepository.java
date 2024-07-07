package hu.davidder.translations.translation.repository;



import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.translations.translation.entity.Translation;

@Repository
public interface TranslationRepository extends CrudRepository<Translation, Long>, CustomTranslationRepository {


}


