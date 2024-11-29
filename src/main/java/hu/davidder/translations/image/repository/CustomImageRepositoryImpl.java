package hu.davidder.translations.image.repository;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.davidder.translations.core.util.CriteriaHelperService;
import hu.davidder.translations.image.entity.Image;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
public class CustomImageRepositoryImpl implements CustomImageRepository {

	@Lazy
	@Autowired
	private EntityManager entityManager;

	@Lazy
	@Autowired
	private CriteriaHelperService<Image> criteriaHelperService;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Image getUrl(String name, Integer targetSize) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Image> cq = cb.createQuery(Image.class);
		Root<Image> root = cq.from(Image.class);
		Set<Predicate> predicates = new HashSet<>();
		predicates.add(cb.equal(root.get("name"), name));
		predicates.add(cb.equal(root.get("targetSize"), targetSize));
		predicates = criteriaHelperService.addAllBasePredicates(cb, root, predicates);
		cq.where(criteriaHelperService.getAsArray(predicates));
		TypedQuery<Image> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}

}