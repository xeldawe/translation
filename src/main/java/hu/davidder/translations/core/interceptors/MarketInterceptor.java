package hu.davidder.translations.core.interceptors;

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

/**
 * The MarketInterceptor class implements the HandlerInterceptor interface to intercept
 * HTTP requests and determine the current market tenant based on request headers or URL patterns.
 */
@Component
@PropertySource("classpath:interceptor.properties")
public class MarketInterceptor implements HandlerInterceptor {

    @Value("${market.regex}")
    private String marketRegex;

    private static final String DEFAULT_TENANT = HibernateConfig.PUBLIC;
    public static final ThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    /**
     * Intercepts the request before it is handled to determine the current market tenant.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param handler  The handler.
     * @return true to continue processing the request.
     * @throws Exception if an error occurs.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	String tenant = findMarketFromUrl(request.getRequestURI()) 
    			.orElseGet(() -> Optional.ofNullable(request.getHeader(Headers.X_MARKET.getName())) 
    					.orElse(null));
        if (tenant != null) {
            setCurrentTenant(tenant.toLowerCase());
        }
        return true;
    }

    /**
     * Finds the market from the URL based on the configured regex pattern.
     *
     * @param path The request path.
     * @return An Optional containing the market if found, otherwise empty.
     */
    private Optional<String> findMarketFromUrl(String path) {
        Matcher matcher = Pattern.compile(marketRegex, Pattern.CASE_INSENSITIVE).matcher(path);
        return matcher.find() ? Optional.of(matcher.group(0).replace("/", "")) : Optional.empty();
    }

    /**
     * Gets the current tenant identifier.
     *
     * @return The current tenant identifier.
     */
    public String getCurrentTenant() {
        return Optional.ofNullable(currentTenant.get()).orElse(DEFAULT_TENANT);
    }

    /**
     * Sets the current tenant identifier.
     *
     * @param tenant The tenant identifier to set.
     */
    public void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    /**
     * Clears the current tenant identifier.
     */
    public void clear() {
        currentTenant.remove();
    }

    //for test
	public void setMarketRegex(String string) {
		this.marketRegex = string;
	}
}
