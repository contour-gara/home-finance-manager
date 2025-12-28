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

-- INSERT INTO expense_event_id
--     (expense_event_id)
-- VALUES
--     ('01KD27JEZQQY88RG18034YZHBV');
--
-- INSERT INTO expenses_year
--     (last_event_id, year)
-- VALUES
--     ('01KD27JEZQQY88RG18034YZHBV', 2026);
--
-- INSERT INTO expenses_month
--     (last_event_id, month)
-- VALUES
--     ('01KD27JEZQQY88RG18034YZHBV', 1);
--
-- INSERT INTO expenses_payer
--     (last_event_id, payer)
-- VALUES
--     ('01KD27JEZQQY88RG18034YZHBV', 'GARA');
--
-- INSERT INTO expenses_category
--     (last_event_id, category)
-- VALUES
--     ('01KD27JEZQQY88RG18034YZHBV', 'RENT');
--
-- INSERT INTO expenses_amount
--     (last_event_id, amount)
-- VALUES
--     ('01KD27JEZQQY88RG18034YZHBV', 1000);
