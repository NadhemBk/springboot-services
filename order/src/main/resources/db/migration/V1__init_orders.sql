CREATE TABLE customer_order (
    id UUID PRIMARY KEY,
    state VARCHAR(20) NOT NULL,
    category VARCHAR(20) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    site_id VARCHAR(255) NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    payment_type VARCHAR(20) NOT NULL,
    payment_iban VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id UUID NOT NULL,
    product_offering_id VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES customer_order(id) ON DELETE CASCADE
);
