package hu.davidder.translations.core.util;

import java.util.Objects;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.davidder.translations.core.config.HibernateConfig;
import hu.davidder.translations.core.interceptors.MarketInterceptor;

/**
 * The CurrentTenantIdentifierResolverImpl class implements the CurrentTenantIdentifierResolver
 * interface to resolve the current tenant identifier based on the MarketInterceptor.
 */
@SuppressWarnings("rawtypes")
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Autowired
    private MarketInterceptor marketInterceptor;

    /**
     * Resolves the current tenant identifier.
     * If no tenant is set in MarketInterceptor, returns the public schema.
     * 
     * @return The current tenant identifier.
     */
    public String resolveCurrentTenantIdentifier() {
        return Objects.requireNonNullElse(marketInterceptor.getCurrentTenant(), HibernateConfig.PUBLIC);
    }

    /**
     * Validates if the existing current sessions are valid.
     * 
     * @return true as the sessions are validated.
     */
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
