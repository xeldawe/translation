package hu.davidder.translations.util.logger.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggerInterceptor implements HandlerInterceptor {

	private static Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info(
				"Pre ->[" + request.getMethod() + "]" + request.getRequestURI() + "[" + request.getRemoteAddr() + "]");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info("Post -> [" + request.getMethod() + "]" + "[" + response.getStatus() + "]" + request.getRequestURI()
				+ "[" + request.getRemoteAddr() + "]");

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (ex != null) {
			logger.error("After ->[" + request.getMethod() + "]" + request.getRequestURI() + "["
					+ request.getRemoteAddr() + "]" + "[exception: " + ex + "]");
			ex.printStackTrace();
		}
	}
}
