ALTER TABLE transactions
    ADD COLUMN goal_id UUID NULL REFERENCES goals (id) ON DELETE CASCADE;

CREATE INDEX idx_transactions_goal_id ON transactions (goal_id);
