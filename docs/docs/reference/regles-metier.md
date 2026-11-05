---
title: Règles métier
sidebar_position: 2
description: Les règles fonctionnelles des commandes fournisseurs et du catalogue, avec leur raison d'être et la couche qui les fait respecter.
---

# Règles métier

Cette page décrit le comportement attendu de RestoForge, indépendamment des
écrans et de l'API. Chaque règle porte un numéro définitif : les tickets, le
code et les tests y font référence. Une règle abandonnée garde son numéro et
est signalée comme telle.

La colonne **Garantie** indique la couche chargée de faire respecter la règle.
Une règle portée par le service des commandes est implémentée avec le module
Commandes. Le vocabulaire employé est défini dans le [lexique](./lexique.md).

## Vue d'ensemble

| N° | Règle | Garantie |
| --- | --- | --- |
| 1 | Une commande, un seul fournisseur | Schéma |
| 2 | Quantité suggérée : ramener le stock à deux fois le seuil | Service |
| 3 | Cycle de statuts figé : `DRAFT` → `SENT` → `RECEIVED` | Schéma et service |
| 4 | Seul un brouillon est modifiable | Service |
| 5 | Le fournisseur se choisit en premier | Écran et service |
| 6 | Une suggestion n'est jamais une commande | Écran et schéma |
| 7 | Les quantités d'un brouillon sont figées à l'ajout | Service |
| 8 | La réception incrémente le stock, dans la même transaction | Service |
| 9 | Reçu = commandé | Schéma |
| 10 | Le prix d'une ligne est figé à sa création | Schéma et service |
| 11 | Pas de doublon de produit dans une commande | Schéma, service et écran |
| 12 | Toutes les lignes viennent du fournisseur de la commande | Service et test d'intégration |
| 13 | Seul le nom d'un fournisseur est obligatoire | Schéma |
| 14 | « Sous le seuil » signifie strictement inférieur au seuil | Entité |

## Commandes fournisseurs

### Règle 1 — Une commande, un seul fournisseur

Une commande fournisseur porte son fournisseur (`supplier_order.supplier_id`,
obligatoire). Il n'est jamais déduit des lignes.

**Pourquoi.** Une commande correspond à un envoi chez un fournisseur : un bon de
commande, une livraison, une facture.

### Règle 2 — Quantité suggérée : ramener le stock à deux fois le seuil

Pour un produit sous le seuil, la quantité suggérée vaut
`max(0, 2 × seuil − stock)`. Elle est calculée à la volée et n'est jamais
enregistrée.

**Pourquoi.** Commander juste de quoi atteindre le seuil ferait repasser le
produit sous le seuil à la première consommation. Le double du seuil laisse une
marge simple à expliquer. Calculée à la demande, la suggestion reflète toujours
le stock courant.

### Règle 3 — Cycle de statuts figé

Une commande passe par `DRAFT` (brouillon), `SENT` (envoyée) puis `RECEIVED`
(reçue), dans cet ordre. Il n'existe pas de statut `CANCELLED`. Une transition
qui saute une étape ou revient en arrière est refusée (409).

**Pourquoi.** Le MVP couvre la boucle nominale « je commande, je reçois ».
L'annulation et ses conséquences sur le stock sont hors périmètre.

### Règle 4 — Seul un brouillon est modifiable

Modifier une commande `SENT` ou `RECEIVED` est refusé (409).

**Pourquoi.** Une commande envoyée engage le fournisseur. La modifier de notre
seul côté ferait diverger ce que le restaurant attend de ce que le fournisseur
livre.

### Règle 5 — Le fournisseur se choisit en premier

La création d'une commande commence par le choix du fournisseur. La sélection
des produits ne propose ensuite que les produits de ce fournisseur.

**Pourquoi.** C'est la conséquence directe des règles 1 et 12 : l'écran
n'affiche que des choix valides.

### Règle 6 — Une suggestion n'est jamais une commande

Le système propose les produits sous le seuil et leur quantité suggérée ;
l'utilisateur décide. Un produit qui n'est pas sous le seuil peut être ajouté
à la main, sans quantité par défaut : la saisie est obligatoire et strictement
positive.

**Pourquoi.** Une quantité pré-remplie finit validée sans avoir été lue. Le
schéma enregistre la décision de l'utilisateur, pas l'origine de la ligne.

### Règle 7 — Les quantités d'un brouillon sont figées à l'ajout

La quantité d'une ligne est fixée quand la ligne est ajoutée. Elle n'est
jamais recalculée, même si le stock évolue ensuite.

**Pourquoi.** Une valeur qui change sans action de l'utilisateur est une valeur
qu'il ne maîtrise plus.

### Règle 8 — La réception incrémente le stock

Le passage en `RECEIVED` ajoute la quantité de chaque ligne au stock du produit,
dans la même transaction que le changement de statut. Le stock ne bouge jamais
au passage en `SENT`.

**Pourquoi.** Le stock représente ce qui est physiquement en réserve : une
marchandise commandée n'est pas encore arrivée. Une transaction unique
garantit qu'une commande ne peut pas être reçue sans que le stock suive, ni
l'inverse.

### Règle 9 — Reçu = commandé

La réception porte sur la totalité de la commande. Il n'existe ni quantité
reçue distincte de la quantité commandée, ni historique des mouvements de
stock.

**Pourquoi.** La réception partielle est hors périmètre du MVP. Le stock est
tenu comme un état courant (voir l'[ADR-005](../adr/adr-005-stock-etat-courant.md)).

### Règle 10 — Le prix d'une ligne est figé à sa création

`supplier_order_line.unit_price` est recopié depuis `product.unit_price` à la
création de la ligne. Modifier un prix au catalogue n'altère jamais une
commande existante. Le prix d'une ligne reste modifiable tant que la commande
est un brouillon.

**Pourquoi.** Une commande engage un montant. Le recalculer après coup
fausserait ce qui a été envoyé au fournisseur.

### Règle 11 — Pas de doublon de produit dans une commande

Un produit figure au plus une fois dans une commande. L'ajout d'un produit déjà
présent est refusé (409), jamais fusionné avec la ligne existante. À l'écran,
un produit déjà ajouté disparaît de la liste de sélection.

**Pourquoi.** Une fusion silencieuse additionnerait des quantités sans que
l'utilisateur le voie.

### Règle 12 — Toutes les lignes viennent du fournisseur de la commande

Le produit d'une ligne appartient au fournisseur de la commande. Ce contrôle
est applicatif et prouvé par un test d'intégration.

**Pourquoi.** L'exprimer en SQL demanderait une clé étrangère composite et une
colonne redondante sur la table la plus volumineuse : une garantie plus forte,
au prix d'un schéma illisible. Le détail figure dans le
[modèle de données](../architecture/modele-de-donnees.md).

## Catalogue et stock

### Règle 13 — Seul le nom d'un fournisseur est obligatoire

Un fournisseur peut être créé avec son seul nom. Ses coordonnées (e-mail,
téléphone, adresse) sont facultatives, et l'e-mail n'est pas unique.

**Pourquoi.** Un fournisseur est souvent créé depuis une facture ou un appel,
avant d'en connaître toutes les coordonnées. Deux fournisseurs peuvent partager
une adresse de commande générique.

### Règle 14 — « Sous le seuil » signifie strictement inférieur au seuil

Un produit est sous le seuil quand son stock est strictement inférieur à son
seuil d'alerte. À égalité, il n'est pas signalé. L'API expose cet indicateur
(`belowThreshold`) ; les applications ne le recalculent pas.

**Pourquoi.** Le seuil est le minimum acceptable : l'atteindre n'est pas encore
en manquer. Calculée à un seul endroit, la règle donne la même réponse dans
StockForge, dans SupplyForge et pour la suggestion de commande.
