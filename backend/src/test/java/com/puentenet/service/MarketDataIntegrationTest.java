package com.puentenet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.puentenet.domain.Instrument;
import com.puentenet.domain.User;
import com.puentenet.domain.dto.InstrumentResponse;
import com.puentenet.repository.FavoriteRepository;
import com.puentenet.repository.InstrumentRepository;
import com.puentenet.service.marketdata.AlphaVantageService;
import com.puentenet.service.marketdata.CoinGeckoService;
import com.puentenet.service.marketdata.MarketDataService;

@ExtendWith(MockitoExtension.class)
class MarketDataIntegrationTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private AlphaVantageService alphaVantageService;

    @Mock
    private CoinGeckoService coinGeckoService;

    @InjectMocks
    private InstrumentService instrumentService;

    @Mock
    private MarketDataScheduler marketDataScheduler;

    private User testUser;
    private Instrument stockInstrument;
    private Instrument cryptoInstrument;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        stockInstrument = new Instrument();
        stockInstrument.setId(1L);
        stockInstrument.setSymbol("AAPL");
        stockInstrument.setName("Apple Inc.");
        stockInstrument.setCurrentPrice(new BigDecimal("150.00"));
        stockInstrument.setDailyChange(new BigDecimal("2.50"));
        stockInstrument.setDailyChangePercent(new BigDecimal("1.67"));
        stockInstrument.setDayHigh(new BigDecimal("155.00"));
        stockInstrument.setDayLow(new BigDecimal("148.00"));
        stockInstrument.setVolume(50000000L);
        stockInstrument.setLastUpdated(Instant.now());
        stockInstrument.setActive(true);

        cryptoInstrument = new Instrument();
        cryptoInstrument.setId(2L);
        cryptoInstrument.setSymbol("BTC");
        cryptoInstrument.setName("Bitcoin");
        cryptoInstrument.setCurrentPrice(new BigDecimal("45000.00"));
        cryptoInstrument.setDailyChange(new BigDecimal("1000.00"));
        cryptoInstrument.setDailyChangePercent(new BigDecimal("2.27"));
        cryptoInstrument.setDayHigh(new BigDecimal("46000.00"));
        cryptoInstrument.setDayLow(new BigDecimal("44000.00"));
        cryptoInstrument.setVolume(25000000000L);
        cryptoInstrument.setLastUpdated(Instant.now());
        cryptoInstrument.setActive(true);

        // Setup market data services
        instrumentService = new InstrumentService();
        // Use reflection to set the private field
        try {
            java.lang.reflect.Field field = InstrumentService.class.getDeclaredField("marketDataServices");
            field.setAccessible(true);
            field.set(instrumentService, Arrays.asList(alphaVantageService, coinGeckoService));
        } catch (Exception e) {
            fail("Failed to setup test");
        }
    }

    @Test
    void testCompleteMarketDataFlow() {
        // Arrange - Setup AlphaVantage for stocks
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);
        when(alphaVantageService.supportsSymbol("MSFT")).thenReturn(true);
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(alphaVantageService.getRateLimitDelay()).thenReturn(12000L);
        when(alphaVantageService.getQuote("AAPL")).thenReturn(stockInstrument);
        when(alphaVantageService.getAvailableSymbols()).thenReturn(Arrays.asList("AAPL", "MSFT"));

        // Arrange - Setup CoinGecko for crypto
        when(coinGeckoService.supportsSymbol("BTC")).thenReturn(true);
        when(coinGeckoService.supportsSymbol("ETH")).thenReturn(true);
        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");
        when(coinGeckoService.getRateLimitDelay()).thenReturn(6100L);
        when(coinGeckoService.getQuote("BTC")).thenReturn(cryptoInstrument);
        when(coinGeckoService.getAvailableSymbols()).thenReturn(Arrays.asList("BTC", "ETH"));

        // Arrange - Setup database
        when(instrumentRepository.findByActiveTrue()).thenReturn(Arrays.asList(stockInstrument, cryptoInstrument));
        when(favoriteRepository.existsByUserAndInstrument(testUser, stockInstrument)).thenReturn(true);
        when(favoriteRepository.existsByUserAndInstrument(testUser, cryptoInstrument)).thenReturn(false);

        // Act - Get all instruments
        List<InstrumentResponse> instruments = instrumentService.getAllInstruments(testUser);

        // Assert
        assertEquals(2, instruments.size());
        assertTrue(instruments.get(0).isFavorite()); // AAPL should be first (favorite)
        assertFalse(instruments.get(1).isFavorite()); // BTC should be second
        assertEquals("AAPL", instruments.get(0).getSymbol());
        assertEquals("BTC", instruments.get(1).getSymbol());
    }

    @Test
    void testServiceOrchestration() {
        // Arrange
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(alphaVantageService.getRateLimitDelay()).thenReturn(12000L);
        when(alphaVantageService.getQuote("AAPL")).thenReturn(stockInstrument);

        when(coinGeckoService.supportsSymbol("BTC")).thenReturn(true);
        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");
        when(coinGeckoService.getRateLimitDelay()).thenReturn(6100L);
        when(coinGeckoService.getQuote("BTC")).thenReturn(cryptoInstrument);

        // Act - Get quotes from different services
        Instrument stockQuote = instrumentService.getQuote("AAPL");
        Instrument cryptoQuote = instrumentService.getQuote("BTC");

        // Assert
        assertEquals("AAPL", stockQuote.getSymbol());
        assertEquals("BTC", cryptoQuote.getSymbol());
        verify(alphaVantageService).getQuote("AAPL");
        verify(coinGeckoService).getQuote("BTC");
    }

    @Test
    void testServiceFallback() {
        // Arrange - AlphaVantage doesn't support BTC
        when(alphaVantageService.supportsSymbol("BTC")).thenReturn(false);
        when(coinGeckoService.supportsSymbol("BTC")).thenReturn(true);
        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");
        when(coinGeckoService.getRateLimitDelay()).thenReturn(6100L);
        when(coinGeckoService.getQuote("BTC")).thenReturn(cryptoInstrument);

        // Act
        Instrument result = instrumentService.getQuote("BTC");

        // Assert
        assertEquals("BTC", result.getSymbol());
        verify(alphaVantageService).supportsSymbol("BTC");
        verify(coinGeckoService).supportsSymbol("BTC");
        verify(coinGeckoService).getQuote("BTC");
    }

    @Test
    void testGetAllAvailableSymbols() {
        // Arrange
        when(alphaVantageService.getAvailableSymbols()).thenReturn(Arrays.asList("AAPL", "MSFT"));
        when(coinGeckoService.getAvailableSymbols()).thenReturn(Arrays.asList("BTC", "ETH"));

        // Act
        List<String> symbols = instrumentService.getAllAvailableSymbols();

        // Assert
        assertEquals(4, symbols.size());
        assertTrue(symbols.contains("AAPL"));
        assertTrue(symbols.contains("MSFT"));
        assertTrue(symbols.contains("BTC"));
        assertTrue(symbols.contains("ETH"));
    }

    @Test
    void testServiceInfo() {
        // Arrange
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(alphaVantageService.getAvailableSymbols()).thenReturn(Arrays.asList("AAPL", "MSFT"));
        when(alphaVantageService.getRateLimitDelay()).thenReturn(12000L);

        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");
        when(coinGeckoService.getAvailableSymbols()).thenReturn(Arrays.asList("BTC"));
        when(coinGeckoService.getRateLimitDelay()).thenReturn(6100L);

        // Act
        List<InstrumentService.ServiceInfo> serviceInfos = instrumentService.getServiceInfo();

        // Assert
        assertEquals(2, serviceInfos.size());
        
        InstrumentService.ServiceInfo alphaVantageInfo = serviceInfos.stream()
            .filter(info -> "AlphaVantage".equals(info.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(alphaVantageInfo);
        assertEquals(2, alphaVantageInfo.getSymbolCount());
        assertEquals(12000L, alphaVantageInfo.getRateLimitDelay());

        InstrumentService.ServiceInfo coinGeckoInfo = serviceInfos.stream()
            .filter(info -> "CoinGecko".equals(info.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(coinGeckoInfo);
        assertEquals(1, coinGeckoInfo.getSymbolCount());
        assertEquals(6100L, coinGeckoInfo.getRateLimitDelay());
    }

    @Test
    void testSymbolSupport() {
        // Arrange
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);
        when(alphaVantageService.supportsSymbol("INVALID")).thenReturn(false);
        when(coinGeckoService.supportsSymbol("INVALID")).thenReturn(false);

        // Act & Assert
        assertTrue(instrumentService.isSymbolSupported("AAPL"));
        assertFalse(instrumentService.isSymbolSupported("INVALID"));
    }

    @Test
    void testGetServiceForSymbol() {
        // Arrange
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");

        // Act
        String serviceName = instrumentService.getServiceForSymbol("AAPL");

        // Assert
        assertEquals("AlphaVantage", serviceName);
    }
} 