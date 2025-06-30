package com.puentenet.service.marketdata;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.puentenet.config.ServiceConfig;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.exceptions.MarketDataException;
import com.puentenet.domain.exceptions.ServiceUnavailableException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class AlphaVantageServiceTest {

    @Mock
    private ServiceConfig serviceConfig;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AlphaVantageService alphaVantageService;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() {
        realObjectMapper = new ObjectMapper();
        when(serviceConfig.getAlphaVantageBaseUrl()).thenReturn("https://www.alphavantage.co/query");
        when(serviceConfig.getAlphaVantageApiKey()).thenReturn("test-api-key");
        when(serviceConfig.getAlphaVantageSymbols()).thenReturn(java.util.List.of("AAPL", "MSFT", "GOOGL"));
    }

    @Test
    void getQuote_Success() throws Exception {
        // Arrange
        String symbol = "AAPL";
        String mockResponse = """
            {
                "Global Quote": {
                    "01. symbol": "AAPL",
                    "02. open": "150.00",
                    "03. high": "155.00",
                    "04. low": "148.00",
                    "05. price": "152.50",
                    "06. volume": "50000000",
                    "07. latest trading day": "2024-01-15",
                    "08. previous close": "149.00",
                    "09. change": "3.50",
                    "10. change percent": "2.35%"
                }
            }
            """;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        JsonNode rootNode = realObjectMapper.readTree(mockResponse);
        when(objectMapper.readTree(mockResponse)).thenReturn(rootNode);

        // Act
        Instrument result = alphaVantageService.getQuote(symbol);

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals("Apple Inc.", result.getName());
        assertEquals(new BigDecimal("152.50"), result.getCurrentPrice());
        assertEquals(new BigDecimal("3.50"), result.getDailyChange());
        assertEquals(new BigDecimal("2.35"), result.getDailyChangePercent());
        assertEquals(new BigDecimal("155.00"), result.getDayHigh());
        assertEquals(new BigDecimal("148.00"), result.getDayLow());
        assertEquals(50000000L, result.getVolume());
        assertNotNull(result.getLastUpdated());
    }

    @Test
    void getQuote_NoDataFound() {
        // Arrange
        String symbol = "INVALID";
        String mockResponse = """
            {
                "Global Quote": {}
            }
            """;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        try {
            JsonNode rootNode = realObjectMapper.readTree(mockResponse);
            when(objectMapper.readTree(mockResponse)).thenReturn(rootNode);
        } catch (Exception e) {
            fail("Failed to parse mock response");
        }

        // Act & Assert
        MarketDataException exception = assertThrows(MarketDataException.class, 
            () -> alphaVantageService.getQuote(symbol));
        assertEquals("No data found for symbol: INVALID", exception.getMessage());
    }

    @Test
    void getQuote_ApiError() {
        // Arrange
        String symbol = "AAPL";
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn("API Error");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(webClientException));

        // Act & Assert
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, 
            () -> alphaVantageService.getQuote(symbol));
        assertEquals("Alpha Vantage API error for symbol: AAPL", exception.getMessage());
    }

    @Test
    void getQuote_ParsingError() throws Exception {
        // Arrange
        String symbol = "AAPL";
        String invalidResponse = "invalid json";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidResponse));

        when(objectMapper.readTree(invalidResponse)).thenThrow(new RuntimeException("JSON parsing error"));

        // Act & Assert
        MarketDataException exception = assertThrows(MarketDataException.class, 
            () -> alphaVantageService.getQuote(symbol));
        assertEquals("Error fetching data for symbol: AAPL", exception.getMessage());
    }

    @Test
    void getAvailableSymbols() {
        // Act
        var symbols = alphaVantageService.getAvailableSymbols();

        // Assert
        assertEquals(3, symbols.size());
        assertTrue(symbols.contains("AAPL"));
        assertTrue(symbols.contains("MSFT"));
        assertTrue(symbols.contains("GOOGL"));
    }

    @Test
    void getServiceName() {
        // Act
        String serviceName = alphaVantageService.getServiceName();

        // Assert
        assertEquals("AlphaVantage", serviceName);
    }

    @Test
    void supportsSymbol_Supported() {
        // Act & Assert
        assertTrue(alphaVantageService.supportsSymbol("AAPL"));
        assertTrue(alphaVantageService.supportsSymbol("aapl")); // Case insensitive
    }

    @Test
    void supportsSymbol_NotSupported() {
        // Act & Assert
        assertFalse(alphaVantageService.supportsSymbol("INVALID"));
    }

    @Test
    void getRateLimitDelay() {
        // Act
        long delay = alphaVantageService.getRateLimitDelay();

        // Assert
        assertEquals(12000, delay); // 12 seconds
    }


} 