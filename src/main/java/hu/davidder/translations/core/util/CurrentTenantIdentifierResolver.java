package hu.davidder.translations.core.util;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.davidder.translations.core.config.HibernateConfig;
import hu.davidder.translations.core.interceptors.MarketInterceptor;

@SuppressWarnings("rawtypes")
@Component
public class CurrentTenantIdentifierResolver implements org.hibernate.context.spi.CurrentTenantIdentifierResolver{
	
	@Autowired
	private MarketInterceptor marketInterceptor;
	
    public String resolveCurrentTenantIdentifier() {
        return Objects.requireNonNullElse(marketInterceptor.getCurrentTenant(), HibernateConfig.PUBLIC);
    }

    public boolean validateExistingCurrentSessions() {
        return true;
    }
}