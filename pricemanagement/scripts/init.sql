-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Price Lists table
CREATE TABLE price_lists (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL,
    customer_category VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_price_lists_product_id ON price_lists(product_id);
CREATE INDEX idx_price_lists_customer_category ON price_lists(customer_category);
CREATE INDEX idx_price_lists_effective_date ON price_lists(effective_date);