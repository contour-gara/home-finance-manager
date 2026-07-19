CREATE TABLE IF NOT EXISTS expense_date (
   expense_id VARCHAR(26) NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY(expense_id),
    FOREIGN KEY(expense_id) REFERENCES expense_id(expense_id)
);
