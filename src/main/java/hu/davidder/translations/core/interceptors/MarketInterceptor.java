package hu.davidder.translations.core.interceptors;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import hu.davidder.translations.core.config.HibernateConfig;
import hu.davidder.translations.core.enums.Headers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MarketInterceptor implements HandlerInterceptor {

	private static final String DEFAULT_TENANT = HibernateConfig.PUBLIC;
	public static final ThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String privateTenant = request.getHeader(Headers.X_MARKET.getName());
		if (privateTenant != null) {
			setCurrentTenant(privateTenant);
		}
		return true;
	}

	public String getCurrentTenant() {
		String tenant = currentTenant.get();
		return Objects.requireNonNullElse(tenant, DEFAULT_TENANT);
	}

	public void setCurrentTenant(String tenant) {
		currentTenant.set(tenant);
	}

	public void clear() {
		currentTenant.remove();
	}

}
