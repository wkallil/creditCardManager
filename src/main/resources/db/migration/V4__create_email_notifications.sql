CREATE TABLE email_notifications (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    reference_month DATE NOT NULL,
    invoice_date DATE NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_email_notifications_owner
        FOREIGN KEY (owner_id)
        REFERENCES owners(id)
);