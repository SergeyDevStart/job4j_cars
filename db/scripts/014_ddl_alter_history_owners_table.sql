ALTER TABLE history_owners
ADD COLUMN IF NOT EXISTS history_id BIGINT NOT NULL
REFERENCES history(id);