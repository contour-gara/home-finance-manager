CREATE TABLE IF NOT EXISTS expenses_year (
    last_event_id VARCHAR(26) NOT NULL,
    year INTEGER NOT NULL,
    PRIMARY KEY(last_event_id),
    FOREIGN KEY(last_event_id) REFERENCES expense_event_id(expense_event_id),
    FOREIGN KEY(year) REFERENCES year(year)
);

CREATE TABLE IF NOT EXISTS expenses_month (
    last_event_id VARCHAR(26) NOT NULL,
    month INTEGER NOT NULL,
    PRIMARY KEY(last_event_id),
    FOREIGN KEY(last_event_id) REFERENCES expense_event_id(expense_event_id),
    FOREIGN KEY(month) REFERENCES month(month)
);

CREATE TABLE IF NOT EXISTS expenses_payer (
    last_event_id VARCHAR(26) NOT NULL,
    payer VARCHAR(100) NOT NULL,
    PRIMARY KEY(last_event_id),
    FOREIGN KEY(last_event_id) REFERENCES expense_event_id(expense_event_id),
    FOREIGN KEY(payer) REFERENCES payer(payer)
);

CREATE TABLE IF NOT EXISTS expenses_category (
    last_event_id VARCHAR(26) NOT NULL,
    category VARCHAR(100) NOT NULL,
    PRIMARY KEY(last_event_id),
    FOREIGN KEY(last_event_id) REFERENCES expense_event_id(expense_event_id),
    FOREIGN KEY(category) REFERENCES category(category)
);

CREATE TABLE IF NOT EXISTS expenses_amount (
    last_event_id VARCHAR(26) NOT NULL,
    amount INTEGER NOT NULL,
    PRIMARY KEY(last_event_id),
    FOREIGN KEY(last_event_id) REFERENCES expense_event_id(expense_event_id)
);
