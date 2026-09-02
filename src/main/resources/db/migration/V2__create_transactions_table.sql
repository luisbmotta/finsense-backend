CREATE TABLE transactions (
    id          UUID PRIMARY KEY,
    user_id     UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    description VARCHAR(200)    NOT NULL,
    amount      NUMERIC(12, 2)  NOT NULL,
    category    VARCHAR(20)     NOT NULL,
    date        DATE            NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT now(),
    CONSTRAINT chk_transactions_category
        CHECK (category IN ('alimentacao', 'transporte', 'lazer', 'saude', 'outros'))
);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);
CREATE INDEX idx_transactions_user_id_date ON transactions (user_id, date DESC);
