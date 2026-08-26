---
id: lexique
title: Lexique
sidebar_position: 1
description: Vocabulaire métier, technique et workflow de RestoForge — référence à consulter quand un terme ou un sigle est ambigu.
---

# Lexique

Cette page regroupe le vocabulaire employé dans le code, les ADR, les issues et
les écrans de RestoForge. Elle fait référence lorsqu'un terme ou un sigle prête
à confusion.

## Vocabulaire métier

### Brouillon (DRAFT)

Premier statut d'une commande fournisseur, et le seul dans lequel elle reste
modifiable. Toute tentative de modification d'une commande `SENT` ou `RECEIVED`
est refusée avec un code HTTP 409.

### Commande client

Commande passée par un client d'un restaurant et portant sur des plats du menu,
matérialisée par la table `customer_order`. Elle relève de MenuForge, hors
périmètre MVP, et ne doit jamais être confondue avec la
[commande fournisseur](#commande-fournisseur) : les deux notions portent le même
mot en français mais vivent dans deux tables distinctes, `customer_order` et
`supplier_order`, la table `order` seule étant proscrite car `order` est un mot
réservé SQL.

### Commande fournisseur

Demande d'approvisionnement adressée à un fournisseur unique, matérialisée par
les tables `supplier_order` et `supplier_order_line`. Elle porte un
`supplier_id` NOT NULL, jamais déduit de ses lignes, et suit le cycle
`DRAFT` → `SENT` → `RECEIVED`.

### Envoyée (SENT)

Statut d'une commande fournisseur transmise au fournisseur. Le passage en `SENT`
rend la commande non modifiable et ne touche pas au niveau de stock.

### Fournisseur

Partenaire auprès duquel le restaurant s'approvisionne, rattaché à ses produits
et à ses commandes. Il est choisi en premier lors de la création d'une commande,
la sélection des produits en découlant.

### Niveau de stock

Quantité d'un produit actuellement détenue par le restaurant. Elle n'est
incrémentée qu'au passage d'une commande fournisseur en `RECEIVED`, dans la même
transaction que le changement de statut.

### Produit

Article référencé au catalogue, décrit par un nom, une unité, un prix, un
fournisseur et un seuil d'alerte. Il porte également son niveau de stock et
l'indicateur « sous le seuil ».

### Quantité suggérée

Quantité proposée à l'ajout d'un produit dans un brouillon, calculée par
`max(0, 2 × seuil − stock actuel)` afin de ramener le stock à deux fois le
seuil d'alerte. Elle est calculée à la volée et n'est jamais persistée ; une
suggestion n'est jamais une commande, le système propose et l'utilisateur
décide.

### Réception partielle

Réception d'une commande fournisseur dans laquelle les quantités livrées
diffèrent des quantités commandées. Elle est hors périmètre MVP : reçu =
commandé, il n'existe ni `received_quantity` ni historique des mouvements de
stock.

### Recette

Décomposition d'un plat en ingrédients. La notion est hors périmètre MVP et
n'existe ni dans le modèle de données ni dans les écrans.

### Reçue (RECEIVED)

Statut terminal d'une commande fournisseur, atteint à la livraison. Le passage
en `RECEIVED` incrémente le niveau de stock des produits commandés, dans la même
transaction que le changement de statut.

### Seuil d'alerte

Quantité minimale d'un produit en dessous de laquelle un réapprovisionnement est
signalé. Elle est saisie sur le produit et sert de base au calcul de la quantité
suggérée.

### Sous le seuil

Indicateur porté par un produit dont le niveau de stock est inférieur à son
seuil d'alerte. Il signale les produits à réapprovisionner, sans déclencher de
commande : l'ajout manuel d'un produit non sous le seuil reste autorisé, avec
saisie obligatoire d'une quantité strictement positive.

## Vocabulaire technique

### ADR (Architecture Decision Record)

Document consignant une décision structurante au format MADR — Contexte,
Décision, Conséquences. Un ADR accepté est immuable : il n'est pas modifié mais
remplacé par un nouvel ADR.

Voir : [ADR-001 — Monorepo](../adr/adr-001-monorepo.md)

### CRUD (Create, Read, Update, Delete)

Ensemble des quatre opérations élémentaires de gestion d'une ressource
persistée. Le MVP expose un CRUD complet sur les fournisseurs et les produits.

### Design token

Valeur nommée d'une décision visuelle — couleur, espacement, typographie —
partagée par toutes les applications du produit. Les tokens sont fournis par
forge-ui.

### DTO (Data Transfer Object)

Objet dédié au transport de données entre l'API et ses clients, distinct de
l'entité persistée. Il fixe ce que l'API expose et découple le contrat du modèle
de données.

### Entité JPA

Classe Java annotée `@Entity`, mappée sur une table de la base par le fournisseur
JPA. Elle décrit le modèle persisté, jamais le contrat exposé aux clients.

Voir : [Bootstrap de l'API Spring Boot](../guides/bootstrap-api-spring-boot.md)

### Flyway

Outil de migration de schéma versionnées, seule autorité sur le schéma de la
base. La configuration fixe `ddl-auto: validate`, ce qui interdit à Hibernate de
créer ou de modifier la moindre table.

Voir : [Bootstrap de l'API Spring Boot](../guides/bootstrap-api-spring-boot.md)

### JPA (Jakarta Persistence API)

Spécification Java de mapping objet-relationnel, implémentée par Hibernate dans
le projet. Spring Data JPA en fournit la couche d'accès.

Voir : [ADR-004 — Socle technique du backend](../adr/adr-004-socle-technique-backend.md)

### Migration

Script SQL versionné décrivant une évolution du schéma de base, appliqué par
Flyway dans l'ordre de ses numéros. Une migration appliquée n'est plus modifiée ;
une évolution passe par une nouvelle migration.

Voir : [Bootstrap de l'API Spring Boot](../guides/bootstrap-api-spring-boot.md)

### MVP (Minimum Viable Product)

Périmètre minimal livrable du produit, ici la boucle Catalogue & Stock →
Commandes fournisseurs. Tout ce qui en sort n'est pas codé mais capturé comme
issue Linear portant le label `hors-mvp`.

Voir : [ADR-003 — Docker d'abord, Kubernetes ensuite](../adr/adr-003-docker-avant-kubernetes.md)

### OpenAPI

Format de description d'une API REST, produit par springdoc à partir du code du
backend. Il sert de contrat unique entre l'API et les fronts, dont les types
TypeScript sont générés.

Voir : [ADR-001 — Monorepo](../adr/adr-001-monorepo.md)

### ORM (Object-Relational Mapping)

Technique de correspondance entre des objets d'un langage et des tables
relationnelles. Hibernate en est l'implémentation utilisée par le backend.

### PWA (Progressive Web App)

Application web installable, capable de fonctionner hors ligne grâce à un service
worker. StockForge est livrée sous cette forme.

### Repository

Interface Spring Data donnant accès aux entités persistées sans écrire soi-même
les requêtes courantes. Elle constitue la couche d'accès aux données du backend.

Voir : [Bootstrap de l'API Spring Boot](../guides/bootstrap-api-spring-boot.md)

### Service

Composant Spring portant les règles métier, situé entre les contrôleurs REST et
les repositories. C'est là que sont appliquées les règles de statut, de calcul
de quantité suggérée et de mise à jour du stock.

### Shadow DOM

Arbre DOM encapsulé attaché à un élément, dont les styles et la structure
n'interagissent pas avec le reste de la page. C'est le mécanisme d'isolation des
composants forge-ui.

### Testcontainers

Bibliothèque démarrant des conteneurs Docker éphémères le temps des tests. Le
projet l'utilise pour exécuter les tests d'intégration contre un vrai PostgreSQL
plutôt qu'une base en mémoire.

Voir : [ADR-004 — Socle technique du backend](../adr/adr-004-socle-technique-backend.md)

### Tranche verticale

Fonctionnalité livrée de bout en bout, de la base de données à l'interface, sur
un périmètre volontairement étroit. Le projet progresse par tranches verticales
avant d'élargir horizontalement.

### Web Component

Composant d'interface défini par les standards du navigateur — élément
personnalisé, Shadow DOM, template — et donc utilisable par n'importe quel
framework. forge-ui est construit sur cette base avec Lit.

## Vocabulaire workflow

### Conventional Commits

Convention de rédaction des messages de commit sous la forme
`type(scope): description`. Le projet la préfixe de l'identifiant de l'issue :
`[RES-XX] type(scope): description`.

### Definition of Done

Liste de critères vérifiables qui déterminent qu'une issue est terminée. Chaque
issue Linear porte la sienne, rédigée avant le début du travail.

### Draft (MR)

État d'une merge request signalant qu'elle n'est pas prête à être fusionnée.
Toute MR du projet est ouverte en draft dès le premier push.

Voir : [ADR-002 — GitLab principal + miroir GitHub](../adr/adr-002-gitlab-miroir-github.md)

### Fast-forward

Stratégie d'intégration qui rejoue les commits d'une branche à la suite de la
cible, sans commit de fusion. Elle impose de rebaser la branche sur `main` avant
acceptation de la MR.

### hors-mvp

Label Linear rouge appliqué aux issues situées hors du périmètre MVP. Il sert à
capturer une idée sans la coder.

### Jalon

Regroupement d'issues Linear correspondant à une étape du projet. Il donne au
backlog une progression lisible par étapes plutôt que par issues isolées.

### Merge Request

Demande d'intégration d'une branche dans `main` sur GitLab, support de la revue.
`main` étant protégée, c'est la seule voie d'intégration : aucun push direct
n'est possible.

Voir : [ADR-002 — GitLab principal + miroir GitHub](../adr/adr-002-gitlab-miroir-github.md)

### Sous-issue

Issue Linear rattachée à une issue parente et couvrant une partie de son
périmètre. Elle permet de découper un travail trop large pour une seule branche.

### Squash

Regroupement des commits d'une branche en un commit unique avant intégration.
Le projet le pratique systématiquement, ce qui garde un historique linéaire sur
`main`.
