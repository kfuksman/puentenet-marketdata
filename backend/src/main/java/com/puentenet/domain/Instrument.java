package com.puentenet.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "instruments")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "daily_change")
    private BigDecimal dailyChange;

    @Column(name = "daily_change_percent")
    private BigDecimal dailyChangePercent;

    @Column(name = "weekly_change")
    private BigDecimal weeklyChange;

    @Column(name = "weekly_change_percent")
    private BigDecimal weeklyChangePercent;

    @Column(name = "day_high")
    private BigDecimal dayHigh;

    @Column(name = "day_low")
    private BigDecimal dayLow;

    @Column(name = "volume")
    private Long volume;

    @Column(name = "market_cap")
    private BigDecimal marketCap;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "is_active")
    private boolean active = true;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
} 