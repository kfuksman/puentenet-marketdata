package com.puentenet.service.marketdata;

import java.util.List;

import com.puentenet.domain.Instrument;

/**
 * Common interface for market data services
 */
public interface MarketDataService {
    
    /**
     * Get stock/crypto quote by symbol
     * @param symbol The symbol to fetch (e.g., "AAPL", "BTC")
     * @return Instrument with market data or null if not found
     */
    Instrument getQuote(String symbol);
    
    /**
     * Get list of available symbols for this service
     * @return List of supported symbols
     */
    List<String> getAvailableSymbols();
    
    /**
     * Get service name for identification
     * @return Service name (e.g., "AlphaVantage", "CoinGecko")
     */
    String getServiceName();
    
    /**
     * Check if this service supports the given symbol
     * @param symbol The symbol to check
     * @return true if supported, false otherwise
     */
    boolean supportsSymbol(String symbol);
    
    /**
     * Get rate limit delay in milliseconds
     * @return Delay to respect API rate limits
     */
    long getRateLimitDelay();
} 