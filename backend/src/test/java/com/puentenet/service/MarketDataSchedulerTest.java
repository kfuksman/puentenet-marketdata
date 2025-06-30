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
import com.puentenet.repository.InstrumentRepository;

@ExtendWith(MockitoExtension.class)
class MarketDataSchedulerTest {

    @Mock
    private InstrumentService instrumentService;

    @Mock
    private InstrumentRepository instrumentRepository;

    @InjectMocks
    private MarketDataScheduler marketDataScheduler;

    private Instrument testInstrument;
    private Instrument updatedInstrument;

    @BeforeEach
    void setUp() {
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

        updatedInstrument = new Instrument();
        updatedInstrument.setId(1L);
        updatedInstrument.setSymbol("AAPL");
        updatedInstrument.setName("Apple Inc.");
        updatedInstrument.setCurrentPrice(new BigDecimal("152.00"));
        updatedInstrument.setDailyChange(new BigDecimal("4.50"));
        updatedInstrument.setDailyChangePercent(new BigDecimal("3.05"));
        updatedInstrument.setDayHigh(new BigDecimal("156.00"));
        updatedInstrument.setDayLow(new BigDecimal("149.00"));
        updatedInstrument.setVolume(55000000L);
        updatedInstrument.setLastUpdated(Instant.now());
        updatedInstrument.setActive(true);
    }

    @Test
    void updateMarketData_Success() {
        // Arrange
        List<String> symbols = Arrays.asList("AAPL", "MSFT");
        when(instrumentService.getAllAvailableSymbols()).thenReturn(symbols);
        when(instrumentService.getQuote("AAPL")).thenReturn(updatedInstrument);
        when(instrumentService.getQuote("MSFT")).thenReturn(updatedInstrument);
        when(instrumentRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testInstrument));
        when(instrumentRepository.findBySymbol("MSFT")).thenReturn(Optional.of(testInstrument));
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(updatedInstrument);

        // Act
        marketDataScheduler.updateMarketData();

        // Assert
        verify(instrumentService).getAllAvailableSymbols();
        verify(instrumentService).getQuote("AAPL");
        verify(instrumentService).getQuote("MSFT");
        verify(instrumentRepository, times(2)).save(any(Instrument.class));
    }

    @Test
    void updateMarketData_WithError() {
        // Arrange
        List<String> symbols = Arrays.asList("AAPL", "INVALID");
        when(instrumentService.getAllAvailableSymbols()).thenReturn(symbols);
        when(instrumentService.getQuote("AAPL")).thenReturn(updatedInstrument);
        when(instrumentService.getQuote("INVALID")).thenThrow(new RuntimeException("API Error"));
        when(instrumentRepository.findBySymbol("AAPL")).thenReturn(Optional.of(testInstrument));
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(updatedInstrument);

        // Act
        marketDataScheduler.updateMarketData();

        // Assert
        verify(instrumentService).getAllAvailableSymbols();
        verify(instrumentService).getQuote("AAPL");
        verify(instrumentService).getQuote("INVALID");
        verify(instrumentRepository).save(any(Instrument.class)); // Only AAPL should be saved
    }

    @Test
    void updateMarketData_EmptySymbols() {
        // Arrange
        List<String> symbols = Arrays.asList();
        when(instrumentService.getAllAvailableSymbols()).thenReturn(symbols);

        // Act
        marketDataScheduler.updateMarketData();

        // Assert
        verify(instrumentService).getAllAvailableSymbols();
        verify(instrumentService, never()).getQuote(anyString());
        verify(instrumentRepository, never()).save(any(Instrument.class));
    }



    @Test
    void initializeInstruments_EmptyDatabase() {
        // Arrange
        when(instrumentRepository.count()).thenReturn(0L);
        List<String> symbols = Arrays.asList("AAPL", "MSFT");
        when(instrumentService.getAllAvailableSymbols()).thenReturn(symbols);
        when(instrumentService.getQuote("AAPL")).thenReturn(updatedInstrument);
        when(instrumentService.getQuote("MSFT")).thenReturn(updatedInstrument);
        when(instrumentRepository.findBySymbol("AAPL")).thenReturn(Optional.empty());
        when(instrumentRepository.findBySymbol("MSFT")).thenReturn(Optional.empty());
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(updatedInstrument);

        // Act
        marketDataScheduler.initializeInstruments();

        // Assert
        verify(instrumentRepository).count();
        verify(instrumentService).getAllAvailableSymbols();
        verify(instrumentService).getQuote("AAPL");
        verify(instrumentService).getQuote("MSFT");
        verify(instrumentRepository, times(2)).save(any(Instrument.class));
    }

    @Test
    void initializeInstruments_NonEmptyDatabase() {
        // Arrange
        when(instrumentRepository.count()).thenReturn(5L);

        // Act
        marketDataScheduler.initializeInstruments();

        // Assert
        verify(instrumentRepository).count();
        verify(instrumentService, never()).getAllAvailableSymbols();
        verify(instrumentService, never()).getQuote(anyString());
        verify(instrumentRepository, never()).save(any(Instrument.class));
    }

    @Test
    void updateMarketData_HandlesNullResponse() {
        // Arrange
        List<String> symbols = Arrays.asList("AAPL", "MSFT");
        when(instrumentService.getAllAvailableSymbols()).thenReturn(symbols);
        when(instrumentService.getQuote("AAPL")).thenReturn(null);
        when(instrumentService.getQuote("MSFT")).thenReturn(updatedInstrument);
        when(instrumentRepository.findBySymbol("MSFT")).thenReturn(Optional.of(testInstrument));
        when(instrumentRepository.save(any(Instrument.class))).thenReturn(updatedInstrument);

        // Act
        marketDataScheduler.updateMarketData();

        // Assert
        verify(instrumentService).getAllAvailableSymbols();
        verify(instrumentService).getQuote("AAPL");
        verify(instrumentService).getQuote("MSFT");
        verify(instrumentRepository).save(any(Instrument.class)); // Only MSFT should be saved
    }

    @Test
    void updateMarketData_HandlesExceptionInGetQuote() {
        // Arrange
        List<String> symbols = Arrays.asList("AAPL");
        when(instrumentService.getAllAvailableSymbols()).thenReturn(symbols);
        when(instrumentService.getQuote("AAPL")).thenThrow(new RuntimeException("Network error"));

        // Act
        marketDataScheduler.updateMarketData();

        // Assert
        verify(instrumentService).getAllAvailableSymbols();
        verify(instrumentService).getQuote("AAPL");
        verify(instrumentRepository, never()).save(any(Instrument.class));
    }
} 