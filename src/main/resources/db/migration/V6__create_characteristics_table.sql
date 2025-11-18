CREATE TABLE characteristics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_characteristics_name ON characteristics(name);

-- Tabla intermedia para la relación many-to-many entre products y characteristics
CREATE TABLE product_characteristics (
    product_id BIGINT NOT NULL,
    characteristic_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, characteristic_id),
    CONSTRAINT fk_product_characteristics_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_characteristics_characteristic FOREIGN KEY (characteristic_id) REFERENCES characteristics(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_characteristics_product_id ON product_characteristics(product_id);
CREATE INDEX idx_product_characteristics_characteristic_id ON product_characteristics(characteristic_id);
