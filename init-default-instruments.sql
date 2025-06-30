-- Default instruments with realistic market data for Puentenet Market Monitor
-- This script inserts default instruments with realistic prices and market data

-- Alpha Vantage Stocks (10 major stocks)
INSERT INTO instruments (symbol, name, current_price, daily_change, daily_change_percent, day_high, day_low, volume, last_updated, is_active) VALUES
('AAPL', 'Apple Inc.', 195.50, 2.30, 1.19, 196.80, 193.20, 45678900, NOW(), true),
('MSFT', 'Microsoft Corporation', 415.20, -1.80, -0.43, 418.50, 413.10, 23456700, NOW(), true),
('GOOGL', 'Alphabet Inc.', 175.80, 0.90, 0.51, 176.40, 174.20, 18923400, NOW(), true),
('AMZN', 'Amazon.com Inc.', 178.90, 3.20, 1.82, 179.50, 175.80, 34567800, NOW(), true),
('TSLA', 'Tesla Inc.', 248.50, -5.20, -2.05, 252.30, 246.10, 67890100, NOW(), true),
('META', 'Meta Platforms Inc.', 485.60, 8.40, 1.76, 487.20, 480.10, 23456700, NOW(), true),
('NVDA', 'NVIDIA Corporation', 118.80, 2.10, 1.80, 119.50, 116.90, 45678900, NOW(), true),
('NFLX', 'Netflix Inc.', 645.20, -12.30, -1.87, 650.10, 642.50, 12345600, NOW(), true),
('JPM', 'JPMorgan Chase & Co.', 198.40, 1.20, 0.61, 199.20, 197.10, 15678900, NOW(), true),
('JNJ', 'Johnson & Johnson', 147.80, -0.80, -0.54, 148.50, 147.20, 9876540, NOW(), true);

-- CoinGecko Cryptocurrencies (10 major cryptocurrencies)
INSERT INTO instruments (symbol, name, current_price, daily_change, daily_change_percent, day_high, day_low, volume, last_updated, is_active) VALUES
('BTC', 'Bitcoin', 107173.00, -960.00, -0.89, 108500.00, 106800.00, 22815841067, NOW(), true),
('ETH', 'Ethereum', 3241.79, -24.21, -0.74, 3280.00, 3220.00, 15961599340, NOW(), true),
('BNB', 'Binance Coin', 657.22, 2.45, 0.37, 660.00, 654.00, 704595909, NOW(), true),
('ADA', 'Cardano', 0.485, -0.015, -3.00, 0.495, 0.480, 456789000, NOW(), true),
('SOL', 'Solana', 155.67, 2.58, 1.69, 158.00, 153.00, 5838632041, NOW(), true),
('XRP', 'Ripple', 0.524, -0.008, -1.50, 0.530, 0.520, 2345678900, NOW(), true),
('DOT', 'Polkadot', 6.85, 0.12, 1.78, 6.90, 6.78, 345678900, NOW(), true),
('DOGE', 'Dogecoin', 0.128, 0.002, 1.59, 0.130, 0.126, 1234567890, NOW(), true),
('AVAX', 'Avalanche', 28.45, -0.35, -1.22, 28.80, 28.20, 567890123, NOW(), true),
('MATIC', 'Polygon', 0.685, 0.015, 2.24, 0.690, 0.680, 789012345, NOW(), true);

-- Note: These are realistic market data values as of recent market conditions
-- Prices, volumes, and market caps are representative of actual market data 