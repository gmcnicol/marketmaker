CREATE TABLE asset
(
    id BIGSERIAL
        CONSTRAINT asset_id PRIMARY KEY,
    name VARCHAR(10),
    value NUMERIC(20,8),
    created_at TIMESTAMP WITHOUT TIME ZONE

);

