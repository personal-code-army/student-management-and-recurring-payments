ALTER TABLE payments
    ADD COLUMN mercado_pago_preference_id VARCHAR(255),
    ADD COLUMN mercado_pago_payment_id    VARCHAR(255),
    ADD COLUMN checkout_url               TEXT,
    ADD COLUMN external_reference         VARCHAR(255),
    ADD COLUMN payer_name                 VARCHAR(255),
    ADD COLUMN payer_email                VARCHAR(255),
    ADD CONSTRAINT uq_payments_external_reference UNIQUE (external_reference);

CREATE INDEX idx_payments_mp_preference ON payments (mercado_pago_preference_id);
CREATE INDEX idx_payments_external_ref  ON payments (external_reference);
