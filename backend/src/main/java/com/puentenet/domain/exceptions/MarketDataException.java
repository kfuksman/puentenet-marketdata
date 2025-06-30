package com.puentenet.domain.exceptions;

/**
 * Excepción personalizada para errores relacionados con datos de mercado
 */
public class MarketDataException extends RuntimeException {
    
    private final String symbol;
    private final String serviceName;
    
    public MarketDataException(String message) {
        super(message);
        this.symbol = null;
        this.serviceName = null;
    }
    
    public MarketDataException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
        this.serviceName = null;
    }
    
    public MarketDataException(String message, String symbol, String serviceName) {
        super(message);
        this.symbol = symbol;
        this.serviceName = serviceName;
    }
    
    public MarketDataException(String message, Throwable cause) {
        super(message, cause);
        this.symbol = null;
        this.serviceName = null;
    }
    
    public MarketDataException(String message, String symbol, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.serviceName = null;
    }
    
    public MarketDataException(String message, String symbol, String serviceName, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.serviceName = serviceName;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String getServiceName() {
        return serviceName;
    }
} 