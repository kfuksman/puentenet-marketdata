package com.puentenet.service.marketdata;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puentenet.config.ServiceConfig;
import com.puentenet.domain.Instrument;
import com.puentenet.domain.exceptions.MarketDataException;
import com.puentenet.domain.exceptions.ServiceUnavailableException;

@Service
public class CoinGeckoService implements MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(CoinGeckoService.class);

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
            // Convert display symbol to CoinGecko ID
            String coinGeckoId = getCoinGeckoId(symbol);
            if (coinGeckoId == null) {
                throw new MarketDataException("Symbol not supported: " + symbol, symbol, "CoinGecko");
            }

            url = String.format("%s/simple/price?ids=%s&vs_currencies=usd&include_24hr_change=true&include_24hr_vol=true&include_last_updated_at=true", 
                serviceConfig.getCoinGeckoBaseUrl(), coinGeckoId);
            
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode coinData = root.get(coinGeckoId);

            if (coinData != null && !coinData.isEmpty()) {
                return parseCryptoQuote(coinData, symbol, coinGeckoId);
            }

            throw new MarketDataException("No data found for symbol: " + symbol, symbol, "CoinGecko");

        } catch (WebClientResponseException e) {
            logger.error("API error for symbol {}: {}", symbol, e.getMessage());
            throw new ServiceUnavailableException("CoinGecko API error for symbol: " + symbol, 
                "CoinGecko", url, e);
        } catch (Exception e) {
            logger.error("Error fetching data for symbol {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Error fetching data for symbol: " + symbol, symbol, "CoinGecko", e);
        }
    }

    @Override
    public List<String> getAvailableSymbols() {
        return new ArrayList<>(serviceConfig.getCoinGeckoSymbols());
    }

    @Override
    public String getServiceName() {
        return "CoinGecko";
    }

    @Override
    public boolean supportsSymbol(String symbol) {
        return serviceConfig.getCoinGeckoSymbols().contains(symbol.toUpperCase());
    }

    @Override
    public long getRateLimitDelay() {
        return 6100; // 6.1 seconds for CoinGecko free tier (10 calls per minute)
    }

    private String getCoinGeckoId(String symbol) {
        // Map configured symbols to CoinGecko IDs
        return switch (symbol.toUpperCase()) {
            case "BTC" -> "bitcoin";
            case "ETH" -> "ethereum";
            case "BNB" -> "binancecoin";
            case "ADA" -> "cardano";
            case "SOL" -> "solana";
            case "XRP" -> "ripple";
            case "DOT" -> "polkadot";
            case "DOGE" -> "dogecoin";
            case "AVAX" -> "avalanche-2";
            case "MATIC" -> "polygon";
            case "LINK" -> "chainlink";
            case "UNI" -> "uniswap";
            case "LTC" -> "litecoin";
            case "XLM" -> "stellar";
            case "ATOM" -> "cosmos";
            case "XMR" -> "monero";
            case "ALGO" -> "algorand";
            case "VET" -> "vechain";
            case "FIL" -> "filecoin";
            case "TRX" -> "tron";
            default -> null;
        };
    }

    private Instrument parseCryptoQuote(JsonNode coinData, String symbol, String coinGeckoId) {
        try {
            Instrument instrument = new Instrument();
            instrument.setSymbol(symbol);
            instrument.setName(getCryptoName(symbol));
            
            // Parse price data
            JsonNode usdNode = coinData.get("usd");
            if (usdNode != null) {
                try {
                    instrument.setCurrentPrice(new BigDecimal(usdNode.asText()));
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse price for {}: {}", symbol, usdNode.asText());
                }
            }

            // Parse 24h change
            JsonNode changeNode = coinData.get("usd_24h_change");
            if (changeNode != null) {
                try {
                    BigDecimal changePercent = new BigDecimal(changeNode.asText());
                    instrument.setDailyChangePercent(changePercent);
                    
                    // Calculate absolute change (approximate)
                    if (instrument.getCurrentPrice() != null) {
                        BigDecimal change = instrument.getCurrentPrice()
                                .multiply(changePercent)
                                .divide(new BigDecimal("100"), 4, BigDecimal.ROUND_HALF_UP);
                        instrument.setDailyChange(change);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse change for {}: {}", symbol, changeNode.asText());
                }
            }

            // Parse 24h volume
            JsonNode volumeNode = coinData.get("usd_24h_vol");
            if (volumeNode != null) {
                try {
                    // Handle scientific notation by parsing as double first
                    double volumeDouble = Double.parseDouble(volumeNode.asText());
                    instrument.setVolume((long) volumeDouble);
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse volume for {}: {}", symbol, volumeNode.asText());
                    instrument.setVolume(0L);
                }
            }

            // Parse last updated
            JsonNode lastUpdatedNode = coinData.get("last_updated_at");
            if (lastUpdatedNode != null) {
                long timestamp = lastUpdatedNode.asLong();
                instrument.setLastUpdated(Instant.now()); // We'll use current time for simplicity
            } else {
                instrument.setLastUpdated(Instant.now());
            }

            return instrument;

        } catch (Exception e) {
            logger.error("Error parsing crypto data for {}: {}", symbol, e.getMessage());
            throw new MarketDataException("Error parsing crypto data for symbol: " + symbol, symbol, "CoinGecko", e);
        }
    }

    private String getCryptoName(String symbol) {
        // Map configured symbols to display names
        return switch (symbol.toUpperCase()) {
            case "BTC" -> "Bitcoin";
            case "ETH" -> "Ethereum";
            case "BNB" -> "Binance Coin";
            case "ADA" -> "Cardano";
            case "SOL" -> "Solana";
            case "XRP" -> "Ripple";
            case "DOT" -> "Polkadot";
            case "DOGE" -> "Dogecoin";
            case "AVAX" -> "Avalanche";
            case "MATIC" -> "Polygon";
            case "LINK" -> "Chainlink";
            case "UNI" -> "Uniswap";
            case "LTC" -> "Litecoin";
            case "XLM" -> "Stellar";
            case "ATOM" -> "Cosmos";
            case "XMR" -> "Monero";
            case "ALGO" -> "Algorand";
            case "VET" -> "VeChain";
            case "FIL" -> "Filecoin";
            case "TRX" -> "TRON";
            default -> symbol;
        };
    }

    // Rate limiting for CoinGecko
    public void rateLimit() {
        try {
            TimeUnit.MILLISECONDS.sleep(getRateLimitDelay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 