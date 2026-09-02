CREATE TABLE users (
    id              UUID PRIMARY KEY,
    name            VARCHAR(120)    NOT NULL,
    email           VARCHAR(180)    NOT NULL UNIQUE,
    password_hash   VARCHAR(100)    NOT NULL,
    monthly_income  NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    created_at      TIMESTAMP       NOT NULL DEFAULT now()
);
