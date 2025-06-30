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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.puentenet.config.ServiceConfig;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.exceptions.MarketDataException;
import com.puentenet.domain.exceptions.ServiceUnavailableException;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CoinGeckoServiceTest {

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
    private CoinGeckoService coinGeckoService;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() {
        realObjectMapper = new ObjectMapper();
        when(serviceConfig.getCoinGeckoBaseUrl()).thenReturn("https://api.coingecko.com/api/v3");
        when(serviceConfig.getCoinGeckoSymbols()).thenReturn(java.util.List.of("bitcoin", "ethereum", "cardano"));
    }

    @Test
    void getQuote_Success() throws Exception {
        // Arrange
        String symbol = "bitcoin";
        String mockResponse = """
            [
                {
                    "id": "bitcoin",
                    "symbol": "btc",
                    "name": "Bitcoin",
                    "current_price": 45000.0,
                    "market_cap": 850000000000,
                    "market_cap_rank": 1,
                    "high_24h": 46000.0,
                    "low_24h": 44000.0,
                    "price_change_24h": 1000.0,
                    "price_change_percentage_24h": 2.27,
                    "total_volume": 25000000000
                }
            ]
            """;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));

        JsonNode rootNode = realObjectMapper.readTree(mockResponse);
        when(objectMapper.readTree(mockResponse)).thenReturn(rootNode);

        // Act
        Instrument result = coinGeckoService.getQuote(symbol);

        // Assert
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals("Bitcoin", result.getName());
        assertEquals(new BigDecimal("45000.0"), result.getCurrentPrice());
        assertEquals(new BigDecimal("1000.0"), result.getDailyChange());
        assertEquals(new BigDecimal("2.27"), result.getDailyChangePercent());
        assertEquals(new BigDecimal("46000.0"), result.getDayHigh());
        assertEquals(new BigDecimal("44000.0"), result.getDayLow());
        assertEquals(25000000000L, result.getVolume());
        assertNotNull(result.getLastUpdated());
    }

    @Test
    void getQuote_NoDataFound() {
        // Arrange
        String symbol = "invalid-coin";
        String mockResponse = "[]";

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
            () -> coinGeckoService.getQuote(symbol));
        assertEquals("No data found for symbol: invalid-coin", exception.getMessage());
    }

    @Test
    void getQuote_ApiError() {
        // Arrange
        String symbol = "bitcoin";
        WebClientResponseException webClientException = mock(WebClientResponseException.class);
        when(webClientException.getMessage()).thenReturn("API Error");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(webClientException));

        // Act & Assert
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, 
            () -> coinGeckoService.getQuote(symbol));
        assertEquals("CoinGecko API error for symbol: bitcoin", exception.getMessage());
    }

    @Test
    void getQuote_ParsingError() throws Exception {
        // Arrange
        String symbol = "bitcoin";
        String invalidResponse = "invalid json";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidResponse));

        when(objectMapper.readTree(invalidResponse)).thenThrow(new RuntimeException("JSON parsing error"));

        // Act & Assert
        MarketDataException exception = assertThrows(MarketDataException.class, 
            () -> coinGeckoService.getQuote(symbol));
        assertEquals("Error fetching data for symbol: bitcoin", exception.getMessage());
    }

    @Test
    void getQuote_EmptyArrayResponse() {
        // Arrange
        String symbol = "bitcoin";
        String mockResponse = "[]";

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
            () -> coinGeckoService.getQuote(symbol));
        assertEquals("No data found for symbol: bitcoin", exception.getMessage());
    }

    @Test
    void getAvailableSymbols() {
        // Act
        var symbols = coinGeckoService.getAvailableSymbols();

        // Assert
        assertEquals(3, symbols.size());
        assertTrue(symbols.contains("bitcoin"));
        assertTrue(symbols.contains("ethereum"));
        assertTrue(symbols.contains("cardano"));
    }

    @Test
    void getServiceName() {
        // Act
        String serviceName = coinGeckoService.getServiceName();

        // Assert
        assertEquals("CoinGecko", serviceName);
    }

    @Test
    void supportsSymbol_Supported() {
        // Act & Assert
        assertTrue(coinGeckoService.supportsSymbol("bitcoin"));
        assertTrue(coinGeckoService.supportsSymbol("ethereum"));
    }

    @Test
    void supportsSymbol_NotSupported() {
        // Act & Assert
        assertFalse(coinGeckoService.supportsSymbol("invalid-coin"));
    }

    @Test
    void getRateLimitDelay() {
        // Act
        long delay = coinGeckoService.getRateLimitDelay();

        // Assert
        assertEquals(6100, delay); // 6.1 seconds
    }


} 