package hu.davidder.translations.core.util;

import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class CriteriaHelperService {

	public Set<Predicate> addAllBasePredicates(CriteriaBuilder cb, Root root,Set<Predicate> predicates){
	    Predicate isNotDeleted = cb.equal(root.get("deleted"), false);
	    Predicate isNotDisabled = cb.equal(root.get("status"), true);
		predicates.add(isNotDeleted);
		predicates.add(isNotDisabled);
		return predicates;
	}
	
	public Predicate[] getAsArray(Set<Predicate> set){
		return set.toArray(new Predicate[0]);
	}
}
	
