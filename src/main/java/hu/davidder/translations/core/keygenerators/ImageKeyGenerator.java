package hu.davidder.translations.core.keygenerators;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

import hu.davidder.translations.core.interceptors.MarketInterceptor;

public class ImageKeyGenerator implements KeyGenerator {
	private static final String UNDERSCORE_DELIMITER = "_";
	public Object generate(Object target, Method method, Object... params) {
		StringBuilder keyStringBuilder = new StringBuilder();
		keyStringBuilder
		.append(params[0]) //NAME
		.append(UNDERSCORE_DELIMITER)
		.append(MarketInterceptor.currentTenant.get())
		.append(UNDERSCORE_DELIMITER)
		.append(params[1]); //TARGET SIZE
	    return keyStringBuilder.toString();
	}
}
