-- V100 — Données de démonstration, réservées au développement.
-- Ce fichier vit dans db/seed, appliqué par Flyway sous le seul profil `dev`.
-- Il ne doit atteindre aucun environnement déployé.
-- La numérotation à partir de 100 laisse la place aux migrations de schéma.

-- Les colonnes id sont GENERATED ALWAYS AS IDENTITY : aucune valeur explicite
-- n'est fournie, et les clés étrangères sont résolues par le nom.

INSERT INTO supplier (name, email, phone, address) VALUES
    ('Metro', 'commandes@metro.example', '0140550100',
     '5 rue des Halles, 94500 Champigny-sur-Marne'),
    -- Fournisseur sans email : les coordonnées facultatives restent nulles.
    ('Planète Fish', NULL, '0298440277',
     'Quai de la Criée, 29900 Concarneau');

INSERT INTO product (name, unit, unit_price, supplier_id, alert_threshold, stock)
SELECT p.name, p.unit, p.unit_price, s.id, p.alert_threshold, p.stock
FROM (VALUES
    ('Farine T55',         'kg',     0.95,  'Metro',        20.000,  45.000),
    -- Sous le seuil : alimente l'écran de suggestion.
    ('Beurre doux',        'kg',     8.40,  'Metro',         6.000,   2.500),
    ('Œufs',               'unité',  0.28,  'Metro',       240.000, 600.000),
    ('Huile de tournesol', 'L',      2.10,  'Metro',        10.000,  24.000),
    -- Sous le seuil également.
    ('Riz arborio',        'kg',     3.60,  'Metro',         8.000,   1.500),
    -- Stock nul : cas limite de la borne basse de ck_product_stock.
    ('Filet de cabillaud', 'kg',    18.50,  'Planète Fish',  5.000,   0.000),
    ('Moules de bouchot',  'kg',     4.20,  'Planète Fish', 10.000,  30.000)
) AS p (name, unit, unit_price, supplier_name, alert_threshold, stock)
JOIN supplier s ON s.name = p.supplier_name;

-- Une commande en DRAFT chez Metro, portant les deux produits Metro sous leur
-- seuil. Les quantités valent la quantité suggérée, max(0, 2 x seuil - stock).
-- Le prix unitaire est recopié du catalogue au moment de la création.
WITH commande AS (
    INSERT INTO supplier_order (supplier_id, status)
    VALUES ((SELECT id FROM supplier WHERE name = 'Metro'), 'DRAFT')
    RETURNING id
),
lignes (product_name, quantity) AS (
    VALUES ('Beurre doux',  9.500),
           ('Riz arborio', 14.500)
)
INSERT INTO supplier_order_line (supplier_order_id, product_id, quantity, unit_price)
SELECT commande.id, product.id, lignes.quantity, product.unit_price
FROM commande
CROSS JOIN lignes
JOIN product ON product.name = lignes.product_name;
