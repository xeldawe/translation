package hu.davidder.translations.translation.repository;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.davidder.translations.core.util.CriteriaHelperService;
import hu.davidder.translations.translation.entity.Translation;
import hu.davidder.translations.translation.entity.Type;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class CustomTranslationRepositoryImpl implements CustomTranslationRepository {

	@Lazy
	@Autowired
	private EntityManager entityManager;

	@Lazy
	@Autowired
	private CriteriaHelperService<Translation> criteriaHelperService;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Translation findByKey(String key) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Translation> cq = cb.createQuery(Translation.class);
		Root<Translation> root = cq.from(Translation.class);
		Set<Predicate> predicates = new HashSet<>();
		predicates.add(cb.equal(root.get("key"), key));
		predicates = criteriaHelperService.addAllBasePredicates(cb, root, predicates);
		cq.where(criteriaHelperService.getAsArray(predicates));
		TypedQuery<Translation> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Translation findByIdAndType(long id, Type type) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Translation> cq = cb.createQuery(Translation.class);
		Root<Translation> root = cq.from(Translation.class);
		Set<Predicate> predicates = new HashSet<>();
		predicates.add(cb.equal(root.get("id"), id));
		predicates.add(cb.equal(root.get("type"), type));
		predicates = criteriaHelperService.addAllBasePredicates(cb, root, predicates);
		cq.where(criteriaHelperService.getAsArray(predicates));
		TypedQuery<Translation> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}

}
