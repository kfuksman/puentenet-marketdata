package com.puentenet.domain.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.puentenet.domain.Instrument;

public class InstrumentResponse {
    private Long id;
    private String symbol;
    private String name;
    private BigDecimal currentPrice;
    private BigDecimal dailyChange;
    private BigDecimal dailyChangePercent;
    private BigDecimal weeklyChange;
    private BigDecimal weeklyChangePercent;
    private BigDecimal dayHigh;
    private BigDecimal dayLow;
    private Long volume;
    private BigDecimal marketCap;
    private Instant lastUpdated;
    private boolean isFavorite;

    public InstrumentResponse(Instrument instrument, boolean isFavorite) {
        this.id = instrument.getId();
        this.symbol = instrument.getSymbol();
        this.name = instrument.getName();
        this.currentPrice = instrument.getCurrentPrice();
        this.dailyChange = instrument.getDailyChange();
        this.dailyChangePercent = instrument.getDailyChangePercent();
        this.weeklyChange = instrument.getWeeklyChange();
        this.weeklyChangePercent = instrument.getWeeklyChangePercent();
        this.dayHigh = instrument.getDayHigh();
        this.dayLow = instrument.getDayLow();
        this.volume = instrument.getVolume();
        this.marketCap = instrument.getMarketCap();
        this.lastUpdated = instrument.getLastUpdated();
        this.isFavorite = isFavorite;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getDailyChange() {
        return dailyChange;
    }

    public void setDailyChange(BigDecimal dailyChange) {
        this.dailyChange = dailyChange;
    }

    public BigDecimal getDailyChangePercent() {
        return dailyChangePercent;
    }

    public void setDailyChangePercent(BigDecimal dailyChangePercent) {
        this.dailyChangePercent = dailyChangePercent;
    }

    public BigDecimal getWeeklyChange() {
        return weeklyChange;
    }

    public void setWeeklyChange(BigDecimal weeklyChange) {
        this.weeklyChange = weeklyChange;
    }

    public BigDecimal getWeeklyChangePercent() {
        return weeklyChangePercent;
    }

    public void setWeeklyChangePercent(BigDecimal weeklyChangePercent) {
        this.weeklyChangePercent = weeklyChangePercent;
    }

    public BigDecimal getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(BigDecimal dayHigh) {
        this.dayHigh = dayHigh;
    }

    public BigDecimal getDayLow() {
        return dayLow;
    }

    public void setDayLow(BigDecimal dayLow) {
        this.dayLow = dayLow;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
} 