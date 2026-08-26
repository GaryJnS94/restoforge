-- V1 — Socle du catalogue : fournisseurs et produits.

CREATE TABLE supplier (
    id      BIGINT       GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR(150) NOT NULL,
    email   VARCHAR(255),
    phone   VARCHAR(30),
    address TEXT,
    CONSTRAINT pk_supplier PRIMARY KEY (id)
);

CREATE TABLE product (
    id              BIGINT        GENERATED ALWAYS AS IDENTITY,
    name            VARCHAR(150)  NOT NULL,
    unit            VARCHAR(20)   NOT NULL,
    unit_price      NUMERIC(10,2) NOT NULL,
    supplier_id     BIGINT        NOT NULL,
    -- Décimaux à trois chiffres : les unités de vente ne sont pas toutes
    -- entières (kg, L).
    alert_threshold NUMERIC(10,3) NOT NULL DEFAULT 0,
    stock           NUMERIC(10,3) NOT NULL DEFAULT 0,
    CONSTRAINT pk_product PRIMARY KEY (id),
    -- RESTRICT : un fournisseur encore référencé par un produit ne peut pas
    -- être supprimé.
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id)
        REFERENCES supplier (id) ON DELETE RESTRICT,
    CONSTRAINT ck_product_unit_price      CHECK (unit_price >= 0),
    CONSTRAINT ck_product_alert_threshold CHECK (alert_threshold >= 0),
    CONSTRAINT ck_product_stock           CHECK (stock >= 0)
);

CREATE INDEX idx_product_supplier ON product (supplier_id);
