package com.puentenet.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.puentenet.domain.dto.InstrumentResponse;
import com.puentenet.domain.Favorite;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.User;
import com.puentenet.domain.exceptions.InstrumentNotFoundException;
import com.puentenet.domain.exceptions.MarketDataException;
import com.puentenet.domain.exceptions.ServiceUnavailableException;
import com.puentenet.repository.FavoriteRepository;
import com.puentenet.repository.InstrumentRepository;
import com.puentenet.service.marketdata.MarketDataService;

@Service
public class InstrumentService {

    private static final Logger logger = LoggerFactory.getLogger(InstrumentService.class);

    @Autowired
    private InstrumentRepository instrumentRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private List<MarketDataService> marketDataServices;

    public List<InstrumentResponse> getAllInstruments(User user) {
        List<Instrument> instruments = instrumentRepository.findByActiveTrue();
        return instruments.stream()
                .map(instrument -> {
                    boolean isFavorite = user != null && favoriteRepository.existsByUserAndInstrument(user, instrument);
                    return new InstrumentResponse(instrument, isFavorite);
                })
                .sorted(Comparator.comparing(InstrumentResponse::isFavorite).reversed())
                .collect(Collectors.toList());
    }

    public List<InstrumentResponse> searchInstruments(String search, User user) {
        List<Instrument> instruments = instrumentRepository.searchInstruments(search);
        return instruments.stream()
                .map(instrument -> {
                    boolean isFavorite = user != null && favoriteRepository.existsByUserAndInstrument(user, instrument);
                    return new InstrumentResponse(instrument, isFavorite);
                })
                .collect(Collectors.toList());
    }

    public InstrumentResponse getInstrumentById(Long id, User user) {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new InstrumentNotFoundException("Instrument not found with ID: " + id, id));
        
        boolean isFavorite = user != null && favoriteRepository.existsByUserAndInstrument(user, instrument);
        return new InstrumentResponse(instrument, isFavorite);
    }

    public List<InstrumentResponse> getUserFavorites(User user) {
        List<Instrument> favorites = favoriteRepository.findInstrumentsByUser(user);
        return favorites.stream()
                .map(instrument -> new InstrumentResponse(instrument, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToFavorites(Long instrumentId, User user) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new InstrumentNotFoundException("Instrument not found with ID: " + instrumentId, instrumentId));

        if (!favoriteRepository.existsByUserAndInstrument(user, instrument)) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setInstrument(instrument);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFromFavorites(Long instrumentId, User user) {
        Instrument instrument = instrumentRepository.findById(instrumentId)
                .orElseThrow(() -> new InstrumentNotFoundException("Instrument not found with ID: " + instrumentId, instrumentId));

        favoriteRepository.deleteByUserAndInstrument(user, instrument);
    }

    /**
     * Get quote from the appropriate service based on symbol
     */
    public Instrument getQuote(String symbol) {
        // First check if we have recent data in database
        var existingInstrument = instrumentRepository.findBySymbol(symbol);
        if (existingInstrument.isPresent()) {
            Instrument instrument = existingInstrument.get();
            if (instrument.getLastUpdated() != null) {
                Duration timeSinceLastUpdate = Duration.between(instrument.getLastUpdated(), Instant.now());
                if (timeSinceLastUpdate.toMinutes() < 15) {
                    logger.debug("Using cached data for {} - last updated {} minutes ago", 
                        symbol, timeSinceLastUpdate.toMinutes());
                    return instrument;
                }
            }
        }

        // If no recent data, fetch from API
        for (MarketDataService service : marketDataServices) {
            if (service.supportsSymbol(symbol)) {
                logger.debug("Fetching fresh data from {} service for symbol: {}", service.getServiceName(), symbol);
                
                // Apply rate limiting
                try {
                    TimeUnit.MILLISECONDS.sleep(service.getRateLimitDelay());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ServiceUnavailableException("Rate limiting interrupted for symbol: " + symbol, 
                        service.getServiceName(), e);
                }
                
                try {
                    Instrument result = service.getQuote(symbol);
                    if (result == null) {
                        throw new MarketDataException("No data returned from " + service.getServiceName() + " for symbol: " + symbol, 
                            symbol, service.getServiceName());
                    }
                    return result;
                } catch (Exception e) {
                    if (e instanceof MarketDataException) {
                        throw e;
                    }
                    throw new MarketDataException("Error fetching data from " + service.getServiceName() + " for symbol: " + symbol, 
                        symbol, service.getServiceName(), e);
                }
            }
        }
        
        throw new InstrumentNotFoundException("No service found that supports symbol: " + symbol, symbol);
    }

    /**
     * Get all available symbols from all services
     */
    public List<String> getAllAvailableSymbols() {
        List<String> allSymbols = new ArrayList<>();
        for (MarketDataService service : marketDataServices) {
            allSymbols.addAll(service.getAvailableSymbols());
        }
        return allSymbols;
    }

    /**
     * Get available symbols from a specific service
     */
    public List<String> getAvailableSymbols(String serviceName) {
        for (MarketDataService service : marketDataServices) {
            if (service.getServiceName().equals(serviceName)) {
                return service.getAvailableSymbols();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Check if a symbol is supported by any service
     */
    public boolean isSymbolSupported(String symbol) {
        return marketDataServices.stream()
                .anyMatch(service -> service.supportsSymbol(symbol));
    }

    /**
     * Get the service that supports a specific symbol
     */
    public String getServiceForSymbol(String symbol) {
        for (MarketDataService service : marketDataServices) {
            if (service.supportsSymbol(symbol)) {
                return service.getServiceName();
            }
        }
        return null;
    }



    /**
     * Inner class to hold service information
     */
    public static class ServiceInfo {
        private final String name;
        private final int symbolCount;
        private final long rateLimitDelay;

        public ServiceInfo(String name, int symbolCount, long rateLimitDelay) {
            this.name = name;
            this.symbolCount = symbolCount;
            this.rateLimitDelay = rateLimitDelay;
        }

        public String getName() {
            return name;
        }

        public int getSymbolCount() {
            return symbolCount;
        }

        public long getRateLimitDelay() {
            return rateLimitDelay;
        }
    }

    /**
     * Get service information
     */
    public List<ServiceInfo> getServiceInfo() {
        List<ServiceInfo> serviceInfos = new ArrayList<>();
        for (MarketDataService service : marketDataServices) {
            serviceInfos.add(new ServiceInfo(
                service.getServiceName(),
                service.getAvailableSymbols().size(),
                service.getRateLimitDelay()
            ));
        }
        return serviceInfos;
    }
} 