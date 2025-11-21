CREATE TABLE monthly_charges (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    owner_id BIGINT NOt NULL,
    amount NUMERIC(10,2) NOT NULL,
    reference_month DATE NOT NULL,
    invoice_date DATE NOT NULL,
    installment_number INTEGER,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_monthly_charges_expense
        FOREIGN KEY (expense_id)
        REFERENCES expenses(id),

    CONSTRAINT fk_monthly_charges_owner
        FOREIGN KEY (owner_id)
        REFERENCES owners(id),

    CONSTRAINT uq_monthly_charges_expense_month
        UNIQUE (expense_id, reference_month)
);