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

import com.puentenet.domain.Favorite;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.User;
import com.puentenet.domain.dto.InstrumentResponse;
import com.puentenet.domain.exceptions.InstrumentNotFoundException;
import com.puentenet.repository.FavoriteRepository;
import com.puentenet.repository.InstrumentRepository;
import com.puentenet.service.marketdata.MarketDataService;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private List<MarketDataService> marketDataServices;

    @Mock
    private MarketDataService alphaVantageService;

    @Mock
    private MarketDataService coinGeckoService;

    @InjectMocks
    private InstrumentService instrumentService;

    private User testUser;
    private Instrument testInstrument;
    private Instrument testCryptoInstrument;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testInstrument = new Instrument();
        testInstrument.setId(1L);
        testInstrument.setSymbol("AAPL");
        testInstrument.setName("Apple Inc.");
        testInstrument.setCurrentPrice(new BigDecimal("150.00"));
        testInstrument.setDailyChange(new BigDecimal("2.50"));
        testInstrument.setDailyChangePercent(new BigDecimal("1.67"));
        testInstrument.setDayHigh(new BigDecimal("155.00"));
        testInstrument.setDayLow(new BigDecimal("148.00"));
        testInstrument.setVolume(50000000L);
        testInstrument.setLastUpdated(Instant.now());
        testInstrument.setActive(true);

        testCryptoInstrument = new Instrument();
        testCryptoInstrument.setId(2L);
        testCryptoInstrument.setSymbol("BTC");
        testCryptoInstrument.setName("Bitcoin");
        testCryptoInstrument.setCurrentPrice(new BigDecimal("45000.00"));
        testCryptoInstrument.setDailyChange(new BigDecimal("1000.00"));
        testCryptoInstrument.setDailyChangePercent(new BigDecimal("2.27"));
        testCryptoInstrument.setDayHigh(new BigDecimal("46000.00"));
        testCryptoInstrument.setDayLow(new BigDecimal("44000.00"));
        testCryptoInstrument.setVolume(25000000000L);
        testCryptoInstrument.setLastUpdated(Instant.now());
        testCryptoInstrument.setActive(true);

        when(marketDataServices).thenReturn(Arrays.asList(alphaVantageService, coinGeckoService));
    }

    @Test
    void getAllInstruments_Success() {
        // Arrange
        List<Instrument> instruments = Arrays.asList(testInstrument, testCryptoInstrument);
        when(instrumentRepository.findByActiveTrue()).thenReturn(instruments);
        when(favoriteRepository.existsByUserAndInstrument(testUser, testInstrument)).thenReturn(true);
        when(favoriteRepository.existsByUserAndInstrument(testUser, testCryptoInstrument)).thenReturn(false);

        // Act
        List<InstrumentResponse> result = instrumentService.getAllInstruments(testUser);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0).isFavorite()); // Favorites first
        assertFalse(result.get(1).isFavorite());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertEquals("BTC", result.get(1).getSymbol());
    }

    @Test
    void searchInstruments_Success() {
        // Arrange
        String searchTerm = "Apple";
        List<Instrument> instruments = Arrays.asList(testInstrument);
        when(instrumentRepository.searchInstruments(searchTerm)).thenReturn(instruments);
        when(favoriteRepository.existsByUserAndInstrument(testUser, testInstrument)).thenReturn(false);

        // Act
        List<InstrumentResponse> result = instrumentService.searchInstruments(searchTerm, testUser);

        // Assert
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertFalse(result.get(0).isFavorite());
    }

    @Test
    void getInstrumentById_Success() {
        // Arrange
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(testInstrument));
        when(favoriteRepository.existsByUserAndInstrument(testUser, testInstrument)).thenReturn(true);

        // Act
        InstrumentResponse result = instrumentService.getInstrumentById(1L, testUser);

        // Assert
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertTrue(result.isFavorite());
    }

    @Test
    void getInstrumentById_NotFound() {
        // Arrange
        when(instrumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        InstrumentNotFoundException exception = assertThrows(InstrumentNotFoundException.class,
            () -> instrumentService.getInstrumentById(999L, testUser));
        assertEquals("Instrument not found with ID: 999", exception.getMessage());
    }

    @Test
    void getUserFavorites_Success() {
        // Arrange
        List<Instrument> favorites = Arrays.asList(testInstrument);
        when(favoriteRepository.findInstrumentsByUser(testUser)).thenReturn(favorites);

        // Act
        List<InstrumentResponse> result = instrumentService.getUserFavorites(testUser);

        // Assert
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertTrue(result.get(0).isFavorite());
    }

    @Test
    void addToFavorites_Success() {
        // Arrange
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(testInstrument));
        when(favoriteRepository.existsByUserAndInstrument(testUser, testInstrument)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(new Favorite());

        // Act
        instrumentService.addToFavorites(1L, testUser);

        // Assert
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_AlreadyFavorite() {
        // Arrange
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(testInstrument));
        when(favoriteRepository.existsByUserAndInstrument(testUser, testInstrument)).thenReturn(true);

        // Act
        instrumentService.addToFavorites(1L, testUser);

        // Assert
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void addToFavorites_InstrumentNotFound() {
        // Arrange
        when(instrumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        InstrumentNotFoundException exception = assertThrows(InstrumentNotFoundException.class,
            () -> instrumentService.addToFavorites(999L, testUser));
        assertEquals("Instrument not found with ID: 999", exception.getMessage());
    }

    @Test
    void removeFromFavorites_Success() {
        // Arrange
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(testInstrument));

        // Act
        instrumentService.removeFromFavorites(1L, testUser);

        // Assert
        verify(favoriteRepository).deleteByUserAndInstrument(testUser, testInstrument);
    }

    @Test
    void removeFromFavorites_InstrumentNotFound() {
        // Arrange
        when(instrumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        InstrumentNotFoundException exception = assertThrows(InstrumentNotFoundException.class,
            () -> instrumentService.removeFromFavorites(999L, testUser));
        assertEquals("Instrument not found with ID: 999", exception.getMessage());
    }

    @Test
    void getQuote_FromDatabase_RecentData() {
        // Arrange
        when(instrumentRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testInstrument));

        // Act
        Instrument result = instrumentService.getQuote("AAPL");

        // Assert
        assertEquals(testInstrument, result);
        verify(marketDataServices, never()).forEach(any());
    }

    @Test
    void getQuote_FromAPI_AlphaVantage() {
        // Arrange
        when(instrumentRepository.findBySymbol("AAPL")).thenReturn(Optional.empty());
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(alphaVantageService.getRateLimitDelay()).thenReturn(12000L);
        when(alphaVantageService.getQuote("AAPL")).thenReturn(testInstrument);

        // Act
        Instrument result = instrumentService.getQuote("AAPL");

        // Assert
        assertEquals(testInstrument, result);
        verify(alphaVantageService).getQuote("AAPL");
    }

    @Test
    void getQuote_FromAPI_CoinGecko() {
        // Arrange
        when(instrumentRepository.findBySymbol("BTC")).thenReturn(Optional.empty());
        when(alphaVantageService.supportsSymbol("BTC")).thenReturn(false);
        when(coinGeckoService.supportsSymbol("BTC")).thenReturn(true);
        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");
        when(coinGeckoService.getRateLimitDelay()).thenReturn(6100L);
        when(coinGeckoService.getQuote("BTC")).thenReturn(testCryptoInstrument);

        // Act
        Instrument result = instrumentService.getQuote("BTC");

        // Assert
        assertEquals(testCryptoInstrument, result);
        verify(coinGeckoService).getQuote("BTC");
    }

    @Test
    void getQuote_NoServiceFound() {
        // Arrange
        when(instrumentRepository.findBySymbol("INVALID")).thenReturn(Optional.empty());
        when(alphaVantageService.supportsSymbol("INVALID")).thenReturn(false);
        when(coinGeckoService.supportsSymbol("INVALID")).thenReturn(false);

        // Act & Assert
        InstrumentNotFoundException exception = assertThrows(InstrumentNotFoundException.class,
            () -> instrumentService.getQuote("INVALID"));
        assertEquals("No service found that supports symbol: INVALID", exception.getMessage());
    }

    @Test
    void getAllAvailableSymbols() {
        // Arrange
        when(alphaVantageService.getAvailableSymbols()).thenReturn(Arrays.asList("AAPL", "MSFT"));
        when(coinGeckoService.getAvailableSymbols()).thenReturn(Arrays.asList("BTC", "ETH"));

        // Act
        List<String> result = instrumentService.getAllAvailableSymbols();

        // Assert
        assertEquals(4, result.size());
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("MSFT"));
        assertTrue(result.contains("BTC"));
        assertTrue(result.contains("ETH"));
    }

    @Test
    void getAvailableSymbols_ByService() {
        // Arrange
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(alphaVantageService.getAvailableSymbols()).thenReturn(Arrays.asList("AAPL", "MSFT"));

        // Act
        List<String> result = instrumentService.getAvailableSymbols("AlphaVantage");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("MSFT"));
    }

    @Test
    void getAvailableSymbols_ServiceNotFound() {
        // Arrange
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");

        // Act
        List<String> result = instrumentService.getAvailableSymbols("UnknownService");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void isSymbolSupported_True() {
        // Arrange
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);

        // Act & Assert
        assertTrue(instrumentService.isSymbolSupported("AAPL"));
    }

    @Test
    void isSymbolSupported_False() {
        // Arrange
        when(alphaVantageService.supportsSymbol("INVALID")).thenReturn(false);
        when(coinGeckoService.supportsSymbol("INVALID")).thenReturn(false);

        // Act & Assert
        assertFalse(instrumentService.isSymbolSupported("INVALID"));
    }

    @Test
    void getServiceForSymbol_Found() {
        // Arrange
        when(alphaVantageService.supportsSymbol("AAPL")).thenReturn(true);
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");

        // Act
        String result = instrumentService.getServiceForSymbol("AAPL");

        // Assert
        assertEquals("AlphaVantage", result);
    }

    @Test
    void getServiceForSymbol_NotFound() {
        // Arrange
        when(alphaVantageService.supportsSymbol("INVALID")).thenReturn(false);
        when(coinGeckoService.supportsSymbol("INVALID")).thenReturn(false);

        // Act
        String result = instrumentService.getServiceForSymbol("INVALID");

        // Assert
        assertNull(result);
    }

    @Test
    void getServiceInfo() {
        // Arrange
        when(alphaVantageService.getServiceName()).thenReturn("AlphaVantage");
        when(alphaVantageService.getAvailableSymbols()).thenReturn(Arrays.asList("AAPL", "MSFT"));
        when(alphaVantageService.getRateLimitDelay()).thenReturn(12000L);
        when(coinGeckoService.getServiceName()).thenReturn("CoinGecko");
        when(coinGeckoService.getAvailableSymbols()).thenReturn(Arrays.asList("BTC"));
        when(coinGeckoService.getRateLimitDelay()).thenReturn(6100L);

        // Act
        List<InstrumentService.ServiceInfo> result = instrumentService.getServiceInfo();

        // Assert
        assertEquals(2, result.size());
        assertEquals("AlphaVantage", result.get(0).getName());
        assertEquals(2, result.get(0).getSymbolCount());
        assertEquals(12000L, result.get(0).getRateLimitDelay());
        assertEquals("CoinGecko", result.get(1).getName());
        assertEquals(1, result.get(1).getSymbolCount());
        assertEquals(6100L, result.get(1).getRateLimitDelay());
    }
} 