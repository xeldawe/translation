package hu.davidder.translation.api.translation.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.davidder.translation.api.translation.entity.Translation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class CustomTranslationRepositoryImpl implements CustomTranslationRepository {

    @Autowired
    private EntityManager entityManager;
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Translation findByKey(String key) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Translation> cq = cb.createQuery(Translation.class);
        Root<Translation> translation = cq.from(Translation.class);
        Predicate keyPredicate = cb.equal(translation.get("key"), key);
        cq.where(keyPredicate);
        TypedQuery<Translation> query = entityManager.createQuery(cq);
        return query.getSingleResult();
	}

}

