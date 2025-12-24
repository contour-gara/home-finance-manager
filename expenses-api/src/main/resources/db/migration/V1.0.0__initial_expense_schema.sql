CREATE TABLE IF NOT EXISTS expense_id (
    expense_id VARCHAR(26) NOT NULL,
    PRIMARY KEY(expense_id)
);

CREATE TABLE IF NOT EXISTS expense_amount (
    expense_id VARCHAR(26) NOT NULL,
    amount INTEGER NOT NULL,
    PRIMARY KEY(expense_id),
    FOREIGN KEY(expense_id) REFERENCES expense_id(expense_id)
);

CREATE TABLE IF NOT EXISTS payer (
    payer VARCHAR(100) NOT NULL,
    PRIMARY KEY(payer)
);

CREATE TABLE IF NOT EXISTS expense_payer (
    expense_id VARCHAR(26) NOT NULL,
    payer VARCHAR(100) NOT NULL,
    PRIMARY KEY(expense_id),
    FOREIGN KEY(expense_id) REFERENCES expense_id(expense_id),
    FOREIGN KEY(payer) REFERENCES payer(payer)
);

CREATE TABLE IF NOT EXISTS category (
    category VARCHAR(100) NOT NULL,
    PRIMARY KEY(category)
);

CREATE TABLE IF NOT EXISTS expense_category (
    expense_id VARCHAR(26) NOT NULL,
    category VARCHAR(100) NOT NULL,
    PRIMARY KEY(expense_id),
    FOREIGN KEY(expense_id) REFERENCES expense_id(expense_id),
    FOREIGN KEY(category) REFERENCES category(category)
);

CREATE TABLE IF NOT EXISTS year (
    year INTEGER NOT NULL,
    PRIMARY KEY(year)
);

CREATE TABLE IF NOT EXISTS month (
    month INTEGER NOT NULL,
    PRIMARY KEY(month)
);

CREATE TABLE IF NOT EXISTS expense_memo (
    expense_id VARCHAR(26) NOT NULL,
    memo VARCHAR(100) NOT NULL,
    PRIMARY KEY(expense_id),
    FOREIGN KEY(expense_id) REFERENCES expense_id(expense_id)
);
