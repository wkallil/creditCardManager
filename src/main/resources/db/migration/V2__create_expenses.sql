CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    installments INTEGER NOT NULL,
    owner_id BIGINT NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    start_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_expenses_owner FOREIGN KEY (owner_id) REFERENCES owners(id)
);