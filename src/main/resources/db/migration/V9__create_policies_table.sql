-- Create policies table
CREATE TABLE policies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_policy_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create index for better query performance
CREATE INDEX idx_policies_product_id ON policies(product_id);
