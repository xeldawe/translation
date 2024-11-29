package hu.davidder.translations.core.interceptors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import hu.davidder.translations.core.config.HibernateConfig;
import hu.davidder.translations.core.enums.Headers;

public class MarketInterceptorTest {

    private MarketInterceptor marketInterceptor;

    @Mock
    private HibernateConfig hibernateConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        marketInterceptor = new MarketInterceptor();
        marketInterceptor.setMarketRegex("(\\/[a-zA-Z]{2}-[a-zA-Z]{2}\\/)");
    }

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
