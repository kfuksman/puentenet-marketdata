package com.puentenet.domain.exceptions;

/**
 * Excepción personalizada para cuando no se encuentra un instrumento
 */
public class InstrumentNotFoundException extends RuntimeException {
    
    private final Long instrumentId;
    private final String symbol;
    
    public InstrumentNotFoundException(String message) {
        super(message);
        this.instrumentId = null;
        this.symbol = null;
    }
    
    public InstrumentNotFoundException(String message, Long instrumentId) {
        super(message);
        this.instrumentId = instrumentId;
        this.symbol = null;
    }
    
    public InstrumentNotFoundException(String message, String symbol) {
        super(message);
        this.instrumentId = null;
        this.symbol = symbol;
    }
    
    public InstrumentNotFoundException(String message, Long instrumentId, String symbol) {
        super(message);
        this.instrumentId = instrumentId;
        this.symbol = symbol;
    }
    
    public Long getInstrumentId() {
        return instrumentId;
    }
    
    public String getSymbol() {
        return symbol;
    }
} 