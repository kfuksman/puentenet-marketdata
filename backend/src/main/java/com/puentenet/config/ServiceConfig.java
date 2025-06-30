package com.puentenet.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class ServiceConfig {

    // JWT Configuration
    private String jwtSecret;
    private Long jwtExpiration;

    // Alpha Vantage Configuration
    private String alphaVantageApiKey;
    private String alphaVantageBaseUrl;
    private Integer alphaVantageDelay;

    // CoinGecko Configuration
    private String coinGeckoBaseUrl;
    private Integer coinGeckoDelay;

    // Market Data Symbols Configuration
    private List<String> alphaVantageSymbols;
    private List<String> coinGeckoSymbols;

    // Server Configuration
    private Integer serverPort;
    private String serverContextPath;

    // Getters and Setters for JWT
    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Long getJwtExpiration() {
        return jwtExpiration;
    }

    public void setJwtExpiration(Long jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }

    // Getters and Setters for Alpha Vantage
    public String getAlphaVantageApiKey() {
        return alphaVantageApiKey;
    }

    public void setAlphaVantageApiKey(String alphaVantageApiKey) {
        this.alphaVantageApiKey = alphaVantageApiKey;
    }

    public String getAlphaVantageBaseUrl() {
        return alphaVantageBaseUrl;
    }

    public void setAlphaVantageBaseUrl(String alphaVantageBaseUrl) {
        this.alphaVantageBaseUrl = alphaVantageBaseUrl;
    }

    public Integer getAlphaVantageDelay() {
        return alphaVantageDelay;
    }

    public void setAlphaVantageDelay(Integer alphaVantageDelay) {
        this.alphaVantageDelay = alphaVantageDelay;
    }

    // Getters and Setters for CoinGecko
    public String getCoinGeckoBaseUrl() {
        return coinGeckoBaseUrl;
    }

    public void setCoinGeckoBaseUrl(String coinGeckoBaseUrl) {
        this.coinGeckoBaseUrl = coinGeckoBaseUrl;
    }

    public Integer getCoinGeckoDelay() {
        return coinGeckoDelay;
    }

    public void setCoinGeckoDelay(Integer coinGeckoDelay) {
        this.coinGeckoDelay = coinGeckoDelay;
    }

    // Getters and Setters for Market Data Symbols
    public List<String> getAlphaVantageSymbols() {
        return alphaVantageSymbols;
    }

    public void setAlphaVantageSymbols(List<String> alphaVantageSymbols) {
        this.alphaVantageSymbols = alphaVantageSymbols;
    }

    public List<String> getCoinGeckoSymbols() {
        return coinGeckoSymbols;
    }

    public void setCoinGeckoSymbols(List<String> coinGeckoSymbols) {
        this.coinGeckoSymbols = coinGeckoSymbols;
    }

    // Getters and Setters for Server
    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerContextPath() {
        return serverContextPath;
    }

    public void setServerContextPath(String serverContextPath) {
        this.serverContextPath = serverContextPath;
    }
} 