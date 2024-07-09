package hu.davidder.translations.util.logger.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import hu.davidder.translations.core.interceptors.MarketInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggerInterceptor implements HandlerInterceptor {

	private static Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info(
				"Pre ->["+MarketInterceptor.currentTenant.get()+"][" + request.getMethod() + "]" + request.getRequestURI() + "[" + request.getRemoteAddr() + "]");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info("Post -> ["+MarketInterceptor.currentTenant.get()+"][" + request.getMethod() + "]" + "[" + response.getStatus() + "]" + request.getRequestURI()
				+ "[" + request.getRemoteAddr() + "]");

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (ex != null) {
			logger.error("After ->["+MarketInterceptor.currentTenant.get()+"][" + request.getMethod() + "]" + request.getRequestURI() + "["
					+ request.getRemoteAddr() + "]" + "[exception: " + ex + "]");
			ex.printStackTrace();
		}
	}
}
