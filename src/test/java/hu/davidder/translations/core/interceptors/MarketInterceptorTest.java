package hu.davidder.translations.core.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hu.davidder.translations.core.config.HibernateConfig;
import hu.davidder.translations.core.enums.Headers;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MarketInterceptor.class)
public class MarketInterceptorTest {

    @Autowired
    private MarketInterceptor marketInterceptor;

    @Mock
    private HibernateConfig hibernateConfig;

    @Test
    public void testPreHandle_WithHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader(Headers.X_MARKET.getName(), "en-gb");

        boolean result = marketInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("en-gb", MarketInterceptor.currentTenant.get());
    }

    @Test
    public void testPreHandle_WithUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("test/en-gb/products");

        boolean result = marketInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("en-gb", MarketInterceptor.currentTenant.get());
    }

    @Test
    public void testPreHandle_HeaderAndUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(Headers.X_MARKET.getName(), "en-th");
        request.setRequestURI("test/hu-hu/products");
        boolean result = marketInterceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("hu-hu", MarketInterceptor.currentTenant.get());
    }

    @Test
    public void testClear() {
        MarketInterceptor.currentTenant.set("en-gb");
        marketInterceptor.clear();
        assertNull(MarketInterceptor.currentTenant.get());
    }
}
