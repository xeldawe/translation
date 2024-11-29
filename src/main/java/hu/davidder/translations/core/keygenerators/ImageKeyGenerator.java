package hu.davidder.translations.core.keygenerators;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

import hu.davidder.translations.core.interceptors.MarketInterceptor;

/**
 * The ImageKeyGenerator class implements the KeyGenerator interface to generate
 * unique cache keys based on the image name, current tenant, and target size.
 */
public class ImageKeyGenerator implements KeyGenerator {

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
        return new StringBuilder()
            .append(params[0]) // NAME
            .append(UNDERSCORE_DELIMITER)
            .append(MarketInterceptor.currentTenant.get())
            .append(UNDERSCORE_DELIMITER)
            .append(params[1]) // TARGET SIZE
            .toString();
    }
}
