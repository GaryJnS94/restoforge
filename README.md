# RestoForge

SaaS B2B de gestion d'approvisionnement pour la restauration.
Suite d'applications partageant un backend unique (Spring Boot 3 + PostgreSQL)
et un design system commun (forge-ui, Web Components Lit).

> 🔗 Dépôt principal : GitLab (CI/CD) — miroir en lecture sur GitHub.

## La suite

| App         | Techno           | Rôle                     | Statut        |
| ----------- | ---------------- | ------------------------ | ------------- |
| StockForge  | Angular 22 (PWA) | Catalogue & Stock        | MVP           |
| SupplyForge | React Native     | Commandes fournisseurs   | MVP (Phase 3) |
| MenuForge   | Angular (PWA)    | Prise de commande client | Hors-MVP      |

## Structure du monorepo

- `api/` — Backend Spring Boot 3 + PostgreSQL
- `web/` — StockForge (Angular 22)
- `mobile/` — SupplyForge (React Native)
- `forge-ui/` — Design system (Lit)
- `docs/` — Documentation (Docusaurus) + ADR

## Conventions

### Branches

Nom auto-généré par Linear : `garyjohnson1994/res-XX-description`
(bouton "Copy git branch name" sur chaque issue).

### Commits

[Conventional Commits](https://www.conventionalcommits.org/) + ID Linear :
`type(scope): description [RES-XX]`

Exemples :

- `feat(api): ajoute l'entité Product [RES-23]`
- `docs(adr): ADR-001 choix du monorepo [RES-2]`
- `chore(infra): docker-compose postgres [RES-5]`

Scopes : `api`, `web`, `mobile`, `forge-ui`, `docs`, `infra`.

### Workflow

Le suivi est dans Linear (équipe RES). Les statuts bougent automatiquement :
MR ouverte → In Progress · review → In Review · merge → Done.
Ne jamais déplacer les statuts à la main.
