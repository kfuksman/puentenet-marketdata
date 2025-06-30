package com.puentenet.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puentenet.domain.Instrument;
import com.puentenet.repository.InstrumentRepository;
import com.puentenet.service.marketdata.MarketDataService;

@Service
public class MarketDataScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataScheduler.class);

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private List<MarketDataService> marketDataServices;

    // Scheduled task to update market data every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void updateMarketData() {
        logger.info("Starting scheduled market data update");
        
        List<String> symbols = instrumentService.getAllAvailableSymbols();
        logger.info("Available symbols to process: {}", symbols);
        
        for (String symbol : symbols) {
            try {
                // Get fresh data directly from market data services
                Instrument apiData = getFreshMarketData(symbol);
                if (apiData != null) {
                    updateOrCreateInstrumentInTransaction(apiData);
                }
                
            } catch (Exception e) {
                logger.error("Error updating data for symbol {}: {}", symbol, e.getMessage());
            }
        }
        
        logger.info("Completed scheduled market data update");
    }

    private Instrument getFreshMarketData(String symbol) {
        logger.debug("Looking for service to handle symbol: {}", symbol);
        for (MarketDataService service : marketDataServices) {
            logger.debug("Checking service: {} for symbol: {}", service.getServiceName(), symbol);
            if (service.supportsSymbol(symbol)) {
                logger.info("Fetching fresh data from {} service for symbol: {}", service.getServiceName(), symbol);
                
                // Apply rate limiting
                try {
                    TimeUnit.MILLISECONDS.sleep(service.getRateLimitDelay());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Rate limiting interrupted for symbol: {}", symbol);
                    return null;
                }
                
                try {
                    Instrument result = service.getQuote(symbol);
                    if (result != null) {
                        return result;
                    }
                } catch (Exception e) {
                    logger.error("Error fetching data from {} service for symbol {}: {}", 
                        service.getServiceName(), symbol, e.getMessage());
                }
            } else {
                logger.debug("Service {} does not support symbol: {}", service.getServiceName(), symbol);
            }
        }
        logger.warn("No service found to handle symbol: {}", symbol);
        return null;
    }

    @Transactional
    private void updateOrCreateInstrument(Instrument apiData) {
        Optional<Instrument> existing = instrumentRepository.findBySymbol(apiData.getSymbol());
        
        if (existing.isPresent()) {
            Instrument instrument = existing.get();
            instrument.setCurrentPrice(apiData.getCurrentPrice());
            instrument.setDailyChange(apiData.getDailyChange());
            instrument.setDailyChangePercent(apiData.getDailyChangePercent());
            instrument.setDayHigh(apiData.getDayHigh());
            instrument.setDayLow(apiData.getDayLow());
            instrument.setVolume(apiData.getVolume());
            instrument.setLastUpdated(apiData.getLastUpdated());
            instrumentRepository.save(instrument);
            logger.info("Updated instrument: {}", apiData.getSymbol());
        } else {
            // Create a new instrument instance to avoid ID conflicts
            Instrument newInstrument = new Instrument();
            newInstrument.setSymbol(apiData.getSymbol());
            newInstrument.setName(apiData.getName());
            newInstrument.setCurrentPrice(apiData.getCurrentPrice());
            newInstrument.setDailyChange(apiData.getDailyChange());
            newInstrument.setDailyChangePercent(apiData.getDailyChangePercent());
            newInstrument.setDayHigh(apiData.getDayHigh());
            newInstrument.setDayLow(apiData.getDayLow());
            newInstrument.setVolume(apiData.getVolume());
            newInstrument.setLastUpdated(apiData.getLastUpdated());
            newInstrument.setActive(true);
            instrumentRepository.save(newInstrument);
            logger.info("Created new instrument: {}", apiData.getSymbol());
        }
    }

    /**
     * Update or create instrument in a separate transaction to avoid affecting other symbols
     */
    private void updateOrCreateInstrumentInTransaction(Instrument apiData) {
        try {
            updateOrCreateInstrument(apiData);
        } catch (Exception e) {
            logger.error("Failed to update/create instrument {}: {}", apiData.getSymbol(), e.getMessage());
            // Don't re-throw the exception to avoid affecting other symbols
        }
    }

    // Initialize instruments on startup
    public void initializeInstruments() {
        logger.info("Initializing instruments database");
        
        // Update all available symbols (default instruments are loaded from SQL)
        updateMarketData();
    }
} 