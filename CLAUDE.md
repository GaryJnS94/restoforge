# RestoForge — contexte projet

SaaS B2B de gestion d'approvisionnement pour la restauration.
Suite d'applications front partageant un backend unique, un contrat OpenAPI
et un design system commun.

## Structure du monorepo

```
api/         API Spring Boot — backend unique, sert tous les fronts
web/         StockForge — Angular (PWA), catalogue & stock
mobile/      SupplyForge — React Native, commandes fournisseurs
forge-ui/    Design system Lit (Web Components + design tokens)
docs/        Docusaurus — documentation et ADR
compose.yaml Orchestration Docker locale (auto-détecté, pas de -f)
```

## Stack (verrouillée)

| Brique | Techno |
|---|---|
| Backend | Spring Boot 4.1 · Java 21 (LTS) · Spring Web, Data JPA, Validation |
| Base de données | PostgreSQL · migrations Flyway |
| Contrat front/back | OpenAPI (springdoc) → types TypeScript générés |
| Front web | Angular 22 · TypeScript strict · signal-first, zoneless, OnPush |
| Mobile | React Native |
| Design system | Lit (Web Components) |
| Infra | Docker + docker compose · GitLab CI/CD |
| Documentation | Docusaurus (v3) |

Kubernetes est explicitement hors périmètre MVP.

## Périmètre MVP

Une seule boucle métier, complète et déployée :

**Catalogue & Stock → Commandes fournisseurs**

1. CRUD fournisseurs, CRUD produits (nom, unité, prix, fournisseur, seuil
   d'alerte), niveau de stock et indicateur « sous le seuil ».
2. Créer une commande fournisseur, suivre ses statuts.

Livraison : la boucle complète arrive d'abord sur StockForge (web), puis
SupplyForge (mobile) comme app terrain.

### Hors périmètre — ne pas implémenter

- MenuForge (prise de commande client : pad serveur, borne, en ligne)
- Notion de recettes (plat → ingrédients)
- Couche IA (extraction de factures, suggestions)
- Multi-restaurant, rôles fins, analytics, paiements
- Réception partielle et écarts de livraison

Toute idée hors périmètre se capture comme issue Linear avec le label
`hors-mvp`, elle ne se code pas.

## Distinction capitale — deux sens du mot « commande »

- **Commande client** = plats du menu (MenuForge, hors-MVP) → `customer_order`
- **Commande fournisseur** = approvisionnement (SupplyForge) → `supplier_order`

Ne jamais les confondre dans le modèle de données ni dans les écrans.
Le nommage des tables est le garde-fou : `supplier_order`,
`supplier_order_line`. `order` seul est proscrit — mot réservé SQL.

## Règles métier validées

1. Une commande = un seul fournisseur. `supplier_id` porté par la commande,
   NOT NULL, jamais déduit des lignes.
2. Quantité suggérée = ramener le stock à 2 × le seuil.
   `max(0, 2 × threshold − currentStock)`, calculée à la volée, jamais stockée.
3. Cycle de statuts figé : `DRAFT` → `SENT` → `RECEIVED`. Pas de `CANCELLED`.
   Transition non séquentielle → 409.
4. Seul `DRAFT` est éditable. Modifier une commande `SENT` ou `RECEIVED` → 409.
5. Le fournisseur est choisi en premier ; la sélection produits en découle.
6. Une suggestion n'est jamais une commande — le système propose, l'utilisateur
   décide. L'ajout manuel d'un produit non sous le seuil est autorisé, sans
   quantité par défaut (saisie obligatoire, `quantity > 0`).
7. Les quantités d'un brouillon sont figées à l'ajout, jamais recalculées.
8. Le passage en `RECEIVED` incrémente le stock, dans la même transaction que
   le changement de statut. Le stock ne bouge jamais au passage en `SENT`.
9. Reçu = commandé. Pas de `received_quantity`, pas d'historique de mouvements
   de stock dans le MVP.

## Conventions de travail

- **Suivi** : Linear, équipe `RES`. Statuts pilotés par le webhook GitLab —
  ne jamais les déplacer à la main.
- **Branches** : nom fourni par l'issue Linear (`garyjohnson1994/res-XX-...`).
- **Commits** : Conventional Commits préfixés de l'ID.
  `[RES-XX] type(scope): description`
- **MR** : titre au même format, ouverte en draft dès le premier push.
  Fast-forward + squash. `main` est protégée, aucun push direct.
- **Remote** : GitLab est le dépôt primaire, GitHub un miroir.

## Règles de documentation

- Une décision structurante (outil, pattern, architecture) s'accompagne d'un
  **ADR écrit dans la même MR** que le code. Une décision sans ADR ne se merge pas.
- Chaque module bootstrapé reçoit un README : comment lancer, comment tester.
- Une page Docusaurus de description s'écrit quand une tranche verticale est
  livrée et stabilisée, pas pendant qu'elle bouge.

## Commandes

```bash
docker compose up -d          # environnement local (compose.yaml auto-détecté)
docker compose ps             # vérifier l'état, attendre 'healthy'
docker compose config         # valider le fichier sans rien lancer

cd api && ./mvnw spring-boot:run   # lancer l'API
cd api && ./mvnw test              # tests backend

cd docs && npm start               # documentation en local
```

## Principes

- Toujours commencer par une tranche verticale fine de bout en bout avant
  d'élargir horizontalement.
- Infrastructure et documentation avant les fonctionnalités.
- Code clair et maintenable plutôt que techniquement impressionnant.
- Livrer une boucle finie et déployée vaut mieux que six modules à moitié faits.
