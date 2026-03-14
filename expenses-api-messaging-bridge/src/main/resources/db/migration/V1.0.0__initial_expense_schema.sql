CREATE TABLE IF NOT EXISTS processed_message_id (
    message_id VARCHAR(19) NOT NULL,
    PRIMARY KEY(message_id)
);

CREATE TABLE IF NOT EXISTS expense_id (
    expense_id VARCHAR(26) NOT NULL,
    message_id VARCHAR(19) NOT NULL,
    PRIMARY KEY(expense_id),
    FOREIGN KEY(message_id) REFERENCES processed_message_id(message_id)
);
