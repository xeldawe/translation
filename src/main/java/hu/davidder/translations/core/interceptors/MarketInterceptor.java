package hu.davidder.translations.core.interceptors;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import hu.davidder.translations.core.config.HibernateConfig;
import hu.davidder.translations.core.enums.Headers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@PropertySource("classpath:interceptor.properties")
public class MarketInterceptor implements HandlerInterceptor {

	@Value("${market.regex:}")
	private String marketRegex;

	
	private static final String DEFAULT_TENANT = HibernateConfig.PUBLIC;
	public static final ThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String privateTenant = request.getHeader(Headers.X_MARKET.getName());
		String pathInfo = request.getRequestURI().substring(request.getContextPath().length());
		var market =findMarketFromUrl(pathInfo);
		if(market.isPresent()) {
			privateTenant = market.get();
		}
		if (privateTenant != null) {
			setCurrentTenant(privateTenant);
		}
		return true;
	}

	private Optional<String> findMarketFromUrl(String path) {
	    Pattern pattern = Pattern.compile(marketRegex, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(path);
	    return matcher.find()?
	    		 Optional.of(matcher.group(0).replaceAll("/", ""))
	    				: Optional.empty();
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
