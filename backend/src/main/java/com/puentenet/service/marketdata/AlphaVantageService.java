package com.puentenet.service.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puentenet.config.ServiceConfig;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.exceptions.MarketDataException;
import com.puentenet.domain.exceptions.ServiceUnavailableException;
import org.springframework.stereotype.Service;

@Service
public class AlphaVantageService implements MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaVantageService.class);

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Instrument getQuote(String symbol) {
        String url = null;
        try {
            url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", 
                serviceConfig.getAlphaVantageBaseUrl(), symbol, serviceConfig.getAlphaVantageApiKey());
            
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode globalQuote = root.get("Global Quote");

            if (globalQuote != null && !globalQuote.isEmpty()) {
                return parseStockQuote(globalQuote, symbol);
            }

            throw new MarketDataException("No data found for symbol: " + symbol, symbol, "AlphaVantage");

        } catch (WebClientResponseException e) {
            logger.error("API error for symbol {}: {}", symbol, e.getMessage());
            throw new ServiceUnavailableException("Alpha Vantage API error for symbol: " + symbol, 
                "AlphaVantage", url, e);
        } catch (Exception e) {
            logger.error("Error fetching data for symbol {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Error fetching data for symbol: " + symbol, symbol, "AlphaVantage", e);
        }
    }

    @Override
    public List<String> getAvailableSymbols() {
        return new ArrayList<>(serviceConfig.getAlphaVantageSymbols());
    }

    @Override
    public String getServiceName() {
        return "AlphaVantage";
    }

    @Override
    public boolean supportsSymbol(String symbol) {
        return serviceConfig.getAlphaVantageSymbols().contains(symbol.toUpperCase());
    }

    @Override
    public long getRateLimitDelay() {
        return 12000; // 12 seconds for Alpha Vantage free tier
    }

    private Instrument parseStockQuote(JsonNode quote, String symbol) {
        try {
            Instrument instrument = new Instrument();
            instrument.setSymbol(symbol);
            instrument.setName(getCompanyName(symbol));
            
            // Parse price data
            String priceStr = quote.get("05. price").asText();
            if (!priceStr.isEmpty()) {
                instrument.setCurrentPrice(new BigDecimal(priceStr));
            }

            // Parse change data
            String changeStr = quote.get("09. change").asText();
            if (!changeStr.isEmpty()) {
                instrument.setDailyChange(new BigDecimal(changeStr));
            }

            String changePercentStr = quote.get("10. change percent").asText();
            if (!changePercentStr.isEmpty()) {
                // Remove % sign and parse
                changePercentStr = changePercentStr.replace("%", "");
                instrument.setDailyChangePercent(new BigDecimal(changePercentStr));
            }

            // Parse volume
            String volumeStr = quote.get("06. volume").asText();
            if (!volumeStr.isEmpty()) {
                instrument.setVolume(Long.parseLong(volumeStr));
            }

            // Parse high/low
            String highStr = quote.get("03. high").asText();
            if (!highStr.isEmpty()) {
                instrument.setDayHigh(new BigDecimal(highStr));
            }

            String lowStr = quote.get("04. low").asText();
            if (!lowStr.isEmpty()) {
                instrument.setDayLow(new BigDecimal(lowStr));
            }

            instrument.setLastUpdated(Instant.now());
            return instrument;

        } catch (Exception e) {
            logger.error("Error parsing quote data for {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Error parsing quote data for symbol: " + symbol, symbol, "AlphaVantage", e);
        }
    }

    private String getCompanyName(String symbol) {
        // Simple mapping for major companies
        return switch (symbol) {
            case "AAPL" -> "Apple Inc.";
            case "MSFT" -> "Microsoft Corporation";
            case "GOOGL" -> "Alphabet Inc.";
            case "AMZN" -> "Amazon.com Inc.";
            case "TSLA" -> "Tesla Inc.";
            case "META" -> "Meta Platforms Inc.";
            case "NVDA" -> "NVIDIA Corporation";
            case "NFLX" -> "Netflix Inc.";
            case "JPM" -> "JPMorgan Chase & Co.";
            case "JNJ" -> "Johnson & Johnson";
            case "V" -> "Visa Inc.";
            case "PG" -> "Procter & Gamble Co.";
            case "UNH" -> "UnitedHealth Group Inc.";
            case "HD" -> "The Home Depot Inc.";
            case "MA" -> "Mastercard Inc.";
            case "DIS" -> "The Walt Disney Company";
            case "PYPL" -> "PayPal Holdings Inc.";
            case "BAC" -> "Bank of America Corp.";
            case "ADBE" -> "Adobe Inc.";
            case "CRM" -> "Salesforce Inc.";
            default -> symbol + " Corporation";
        };
    }

    // Rate limiting - Alpha Vantage free tier allows 5 calls per minute
    public void rateLimit() {
        try {
            TimeUnit.MILLISECONDS.sleep(getRateLimitDelay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 