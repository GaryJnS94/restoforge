-- V2 — Commandes fournisseurs : en-têtes et lignes.

CREATE TABLE supplier_order (
    id          BIGINT      GENERATED ALWAYS AS IDENTITY,
    -- Une commande porte un fournisseur unique, jamais déduit de ses lignes.
    supplier_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    sent_at     TIMESTAMPTZ,
    received_at TIMESTAMPTZ,
    CONSTRAINT pk_supplier_order PRIMARY KEY (id),
    CONSTRAINT fk_supplier_order_supplier FOREIGN KEY (supplier_id)
        REFERENCES supplier (id) ON DELETE RESTRICT,
    -- Cycle de statuts figé : DRAFT -> SENT -> RECEIVED, sans annulation.
    CONSTRAINT ck_supplier_order_status
        CHECK (status IN ('DRAFT', 'SENT', 'RECEIVED'))
);

CREATE TABLE supplier_order_line (
    id                BIGINT        GENERATED ALWAYS AS IDENTITY,
    supplier_order_id BIGINT        NOT NULL,
    product_id        BIGINT        NOT NULL,
    quantity          NUMERIC(10,3) NOT NULL,
    unit_price        NUMERIC(10,2) NOT NULL,
    CONSTRAINT pk_order_line PRIMARY KEY (id),
    -- CASCADE : une ligne n'a pas d'existence hors de sa commande.
    CONSTRAINT fk_order_line_supplier_order FOREIGN KEY (supplier_order_id)
        REFERENCES supplier_order (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_line_product FOREIGN KEY (product_id)
        REFERENCES product (id) ON DELETE RESTRICT,
    -- Un produit ne figure qu'une fois par commande : la quantité s'ajuste sur
    -- la ligne existante.
    CONSTRAINT uq_order_line_product UNIQUE (supplier_order_id, product_id),
    -- Toute ligne porte une quantité saisie, strictement positive.
    CONSTRAINT ck_order_line_quantity   CHECK (quantity > 0),
    CONSTRAINT ck_order_line_unit_price CHECK (unit_price >= 0)
);

CREATE INDEX idx_supplier_order_supplier ON supplier_order (supplier_id);
-- Pas d'index sur supplier_order_line(supplier_order_id) : uq_order_line_product
-- l'indexe déjà en colonne de tête.
CREATE INDEX idx_order_line_product      ON supplier_order_line (product_id);
