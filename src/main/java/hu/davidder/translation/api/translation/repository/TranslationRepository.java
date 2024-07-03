package hu.davidder.translation.api.translation.repository;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.translation.api.translation.entity.Translation;

@Repository
public interface TranslationRepository extends CrudRepository<Translation, Long>, CustomTranslationRepository {


}


