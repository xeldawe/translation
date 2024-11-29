package hu.davidder.translations.core.keygenerators;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import hu.davidder.translations.core.interceptors.MarketInterceptor;

/**
 * The MarketKeyGenerator class implements the KeyGenerator interface to generate
 * unique cache keys based on the class name, method name, current tenant, and method parameters.
 */
public class MarketKeyGenerator implements KeyGenerator {

    private static final String UNDERSCORE_DELIMITER = "_";

    /**
     * Generates a unique key for the given target, method, and parameters.
     * 
     * @param target The target object.
     * @param method The method being called.
     * @param params The parameters of the method.
     * @return A unique key as an Object.
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + UNDERSCORE_DELIMITER
                + method.getName() + UNDERSCORE_DELIMITER + MarketInterceptor.currentTenant.get() + UNDERSCORE_DELIMITER
                + StringUtils.arrayToDelimitedString(params, UNDERSCORE_DELIMITER);
    }
}
