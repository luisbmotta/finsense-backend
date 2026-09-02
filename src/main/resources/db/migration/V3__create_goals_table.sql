CREATE TABLE goals (
    id             UUID PRIMARY KEY,
    user_id        UUID            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name           VARCHAR(120)    NOT NULL,
    target_amount  NUMERIC(12, 2)  NOT NULL,
    current_amount NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    emoji          VARCHAR(16)     NOT NULL,
    deadline       DATE            NOT NULL,
    color          VARCHAR(9)      NOT NULL,
    created_at     TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_goals_user_id ON goals (user_id);
