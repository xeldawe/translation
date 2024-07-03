package hu.davidder.translation.api.image.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hu.davidder.translation.api.image.entity.Image;
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

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Image getUrl(String name, Integer targetSize) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Image> query = cb.createQuery(Image.class);
		Root<Image> root = query.from(Image.class);
	    Predicate namePredicate = cb.equal(root.get("name"), name);
	    Predicate targetSizePredicate = cb.equal(root.get("targetSize"), targetSize);
		query.where(namePredicate,targetSizePredicate);
        TypedQuery<Image> q = entityManager.createQuery(query);
        return q.getSingleResult();
	}

}