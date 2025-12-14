CREATE TABLE IF NOT EXISTS balance (
    balance_id VARCHAR(26) PRIMARY KEY,
    lender VARCHAR(100) NOT NULL,
    borrower VARCHAR(100) NOT NULL,
    amount INTEGER NOT NULL,
    last_event_id VARCHAR(255) NOT NULL
);
