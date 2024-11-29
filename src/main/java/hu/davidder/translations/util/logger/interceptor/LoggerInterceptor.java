package hu.davidder.translations.util.logger.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import hu.davidder.translations.core.interceptors.MarketInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The LoggerInterceptor class implements the HandlerInterceptor interface
 * to log HTTP request and response details for monitoring and debugging purposes.
 */
@Component
public class LoggerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInterceptor.class);

    /**
     * Logs request details before the request is handled.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param handler The handler.
     * @return true to continue processing the request.
     * @throws Exception if an error occurs.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Pre -> [{}][{}] {} [{}]", MarketInterceptor.currentTenant.get(), request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    /**
     * Logs response details after the request is handled.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param handler The handler.
     * @param modelAndView The model and view.
     * @throws Exception if an error occurs.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("Post -> [{}][{}][{}] {} [{}]", MarketInterceptor.currentTenant.get(), request.getMethod(), response.getStatus(), request.getRequestURI(), request.getRemoteAddr());
    }

    /**
     * Logs details after the completion of request processing, including any exceptions thrown.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param handler The handler.
     * @param ex The exception thrown, if any.
     * @throws Exception if an error occurs.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            logger.error("After -> [{}][{}] {} [{}][exception: {}]", MarketInterceptor.currentTenant.get(), request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), ex);
            ex.printStackTrace();
        }
    }
}
