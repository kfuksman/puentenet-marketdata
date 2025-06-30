-- PostgreSQL indexes and optimizations for Puentenet Market Monitor
-- This script creates indexes for better performance

-- Indexes for users table
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Indexes for instruments table
CREATE INDEX IF NOT EXISTS idx_instruments_symbol ON instruments(symbol);
CREATE INDEX IF NOT EXISTS idx_instruments_is_active ON instruments(is_active);
CREATE INDEX IF NOT EXISTS idx_instruments_last_updated ON instruments(last_updated);

-- Indexes for favorites table
CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_favorites_instrument_id ON favorites(instrument_id);
CREATE INDEX IF NOT EXISTS idx_favorites_created_at ON favorites(created_at);

-- Full text search index for instruments (for search functionality)
CREATE INDEX IF NOT EXISTS idx_instruments_search ON instruments USING gin(to_tsvector('english', name || ' ' || symbol));

-- Optimize table statistics
ANALYZE users;
ANALYZE instruments;
ANALYZE favorites; 