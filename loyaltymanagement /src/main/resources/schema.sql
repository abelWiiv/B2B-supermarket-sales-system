-- Create the loyalty_account table referencing the correct 'customers' table
CREATE TABLE loyalty_account (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE,
    points_balance INTEGER NOT NULL DEFAULT 0,
);

-- Create an index on customer_id in the loyalty_account table
CREATE INDEX idx_loyalty_customer_id ON loyalty_account(customer_id);


-- Create the loyalty_transaction table with references to loyalty_account
  CREATE TABLE loyalty_transaction (
      id UUID PRIMARY KEY,
      customer_id UUID NOT NULL,x
      transaction_type VARCHAR(20) NOT NULL,
      points INTEGER NOT NULL,
      description VARCHAR(255),
      transaction_date TIMESTAMP NOT NULL,
      related_customer_id UUID,
      FOREIGN KEY (customer_id) REFERENCES loyalty_account(customer_id)
 );


