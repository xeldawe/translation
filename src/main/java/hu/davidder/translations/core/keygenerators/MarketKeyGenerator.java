package hu.davidder.translations.core.keygenerators;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import hu.davidder.translations.core.interceptors.MarketInterceptor;

public class MarketKeyGenerator implements KeyGenerator{
    
    private static final String UNDERSCORE_DELIMITER = "_";
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + UNDERSCORE_DELIMITER
          + method.getName() + UNDERSCORE_DELIMITER+MarketInterceptor.currentTenant.get()+UNDERSCORE_DELIMITER
          + StringUtils.arrayToDelimitedString(params, UNDERSCORE_DELIMITER);
    }

}