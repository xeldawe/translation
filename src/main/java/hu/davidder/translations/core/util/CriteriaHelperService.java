package hu.davidder.translations.core.util;

import java.util.Set;

import org.springframework.stereotype.Service;

import hu.davidder.translations.core.base.EntityBase;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * The CriteriaHelperService class provides utility methods to assist with building
 * criteria queries for entities extending EntityBase.
 */
@Service
public class CriteriaHelperService<T extends EntityBase> {

    /**
     * Adds base predicates to the given set of predicates.
     * 
     * @param cb The CriteriaBuilder used to create predicates.
     * @param root The root type in the from clause.
     * @param predicates The set of predicates to add base predicates to.
     * @return The set of predicates with added base predicates.
     */
    public Set<Predicate> addAllBasePredicates(CriteriaBuilder cb, Root<T> root, Set<Predicate> predicates) {
        predicates.add(cb.equal(root.get("deleted"), false));
        predicates.add(cb.equal(root.get("status"), true));
        return predicates;
    }

    /**
     * Converts a set of predicates to an array of predicates.
     * 
     * @param set The set of predicates to convert.
     * @return An array of predicates.
     */
    public Predicate[] getAsArray(Set<Predicate> set) {
        return set.toArray(new Predicate[0]);
    }
}
