-- Default instruments with realistic market data for Puentenet Market Monitor
-- This script inserts default instruments with realistic prices and market data

-- Alpha Vantage Stocks (10 major stocks)
INSERT INTO instruments (symbol, name, current_price, daily_change, daily_change_percent, weekly_change, weekly_change_percent, day_high, day_low, volume, market_cap, last_updated, is_active) VALUES
('AAPL', 'Apple Inc.', 195.50, 2.30, 1.19, 8.50, 4.55, 196.80, 193.20, 45678900, 3050000000000, NOW(), true),
('MSFT', 'Microsoft Corporation', 415.20, -1.80, -0.43, 12.30, 3.05, 418.50, 413.10, 23456700, 3080000000000, NOW(), true),
('GOOGL', 'Alphabet Inc.', 175.80, 0.90, 0.51, 5.20, 3.05, 176.40, 174.20, 18923400, 2220000000000, NOW(), true),
('AMZN', 'Amazon.com Inc.', 178.90, 3.20, 1.82, 15.40, 9.42, 179.50, 175.80, 34567800, 1860000000000, NOW(), true),
('TSLA', 'Tesla Inc.', 248.50, -5.20, -2.05, -12.30, -4.72, 252.30, 246.10, 67890100, 790000000000, NOW(), true),
('META', 'Meta Platforms Inc.', 485.60, 8.40, 1.76, 25.80, 5.61, 487.20, 480.10, 23456700, 1230000000000, NOW(), true),
('NVDA', 'NVIDIA Corporation', 118.80, 2.10, 1.80, 8.90, 8.10, 119.50, 116.90, 45678900, 2920000000000, NOW(), true),
('NFLX', 'Netflix Inc.', 645.20, -12.30, -1.87, -8.50, -1.30, 650.10, 642.50, 12345600, 278000000000, NOW(), true),
('JPM', 'JPMorgan Chase & Co.', 198.40, 1.20, 0.61, 3.80, 1.95, 199.20, 197.10, 15678900, 572000000000, NOW(), true),
('JNJ', 'Johnson & Johnson', 147.80, -0.80, -0.54, -2.10, -1.40, 148.50, 147.20, 9876540, 357000000000, NOW(), true);

-- CoinGecko Cryptocurrencies (10 major cryptocurrencies)
INSERT INTO instruments (symbol, name, current_price, daily_change, daily_change_percent, weekly_change, weekly_change_percent, day_high, day_low, volume, market_cap, last_updated, is_active) VALUES
('BTC', 'Bitcoin', 107173.00, -960.00, -0.89, -2500.00, -2.28, 108500.00, 106800.00, 22815841067, 2100000000000, NOW(), true),
('ETH', 'Ethereum', 3241.79, -24.21, -0.74, -85.50, -2.57, 3280.00, 3220.00, 15961599340, 389000000000, NOW(), true),
('BNB', 'Binance Coin', 657.22, 2.45, 0.37, 15.80, 2.46, 660.00, 654.00, 704595909, 101000000000, NOW(), true),
('ADA', 'Cardano', 0.485, -0.015, -3.00, -0.025, -4.90, 0.495, 0.480, 456789000, 17200000000, NOW(), true),
('SOL', 'Solana', 155.67, 2.58, 1.69, 12.30, 8.57, 158.00, 153.00, 5838632041, 72000000000, NOW(), true),
('XRP', 'Ripple', 0.524, -0.008, -1.50, -0.012, -2.24, 0.530, 0.520, 2345678900, 29000000000, NOW(), true),
('DOT', 'Polkadot', 6.85, 0.12, 1.78, 0.35, 5.38, 6.90, 6.78, 345678900, 8700000000, NOW(), true),
('DOGE', 'Dogecoin', 0.128, 0.002, 1.59, 0.008, 6.67, 0.130, 0.126, 1234567890, 18400000000, NOW(), true),
('AVAX', 'Avalanche', 28.45, -0.35, -1.22, -1.20, -4.05, 28.80, 28.20, 567890123, 11000000000, NOW(), true),
('MATIC', 'Polygon', 0.685, 0.015, 2.24, 0.025, 3.79, 0.690, 0.680, 789012345, 6800000000, NOW(), true);

-- Note: These are realistic market data values as of recent market conditions
-- Prices, volumes, and market caps are representative of actual market data 