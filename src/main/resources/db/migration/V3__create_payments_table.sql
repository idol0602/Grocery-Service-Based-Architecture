CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL,
                          momo_order_id VARCHAR(100) NOT NULL UNIQUE,
                          request_id VARCHAR(100) NOT NULL,
                          amount NUMERIC(10, 2) NOT NULL,
                          status VARCHAR(50) DEFAULT 'PENDING' NOT NULL,
                          pay_url TEXT,
                          transaction_id VARCHAR(100),
                          result_code INTEGER,
                          message TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE RESTRICT
);

CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_momo_order_id ON payments(momo_order_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TRIGGER payments_updated_at_trigger BEFORE UPDATE ON payments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
