ALTER TABLE users ADD COLUMN cpf VARCHAR(500);
ALTER TABLE users ADD COLUMN reset_token VARCHAR(255);
ALTER TABLE users ADD COLUMN reset_token_expiry TIMESTAMP;
CREATE UNIQUE INDEX ux_users_reset_token ON users (reset_token) WHERE reset_token IS NOT NULL;
CREATE INDEX idx_users_reset_token_expiry ON users (reset_token_expiry);
