# ADR-001 — Monorepo

- **Statut** : accepté
- **Date** : 2026-07-11

## Contexte

RestoForge est une suite de plusieurs applications front (StockForge en
Angular, SupplyForge en React Native, forge-ui en Lit) qui consomment toutes
le même backend Spring Boot via un contrat OpenAPI unique, avec des types
TypeScript générés à partir de ce contrat.

Deux options d'organisation du code ont été envisagées :

- **Multi-repos (polyrepo)** : un dépôt Git par brique (api, web, mobile,
  forge-ui, docs). Le contrat OpenAPI et les types générés devraient alors
  être publiés comme package versionné (registry npm) pour être partagés
  entre les dépôts.
- **Monorepo** : un seul dépôt Git contenant toutes les briques dans des
  dossiers séparés.

Le projet est porté par une équipe réduite, avec un objectif de livraison
rapide d'un MVP ; la coordination entre dépôts multiples représenterait un
coût disproportionné à ce stade.

## Décision

Un seul dépôt Git contient l'ensemble du projet (`api/`, `web/`, `mobile/`,
`forge-ui/`, `docs/`). Le contrat OpenAPI est versionné à un seul endroit
et les types TypeScript sont générés localement par chaque application
front.

## Conséquences

**Positives :**

- Un changement qui traverse plusieurs applications (ex. renommer un champ
  de l'API) tient dans un seul commit / une seule MR — atomique et traçable.
- Aucune gestion de versions de packages entre les briques : le contrat
  OpenAPI est toujours synchronisé avec le code qui le consomme.
- Une seule configuration Git, une seule CI, un seul dépôt à cloner —
  onboarding simplifié.

**Négatives (assumées) :**

- La CI devra être configurée finement (`rules: changes`) pour ne pas
  rebuilder toutes les briques à chaque commit.
- L'historique Git mélange toutes les briques ; la convention de scopes
  dans les commits (`api`, `web`, `docs`…) devient indispensable pour
  s'y retrouver.
- Cette organisation convient à une équipe réduite sur un périmètre MVP ;
  elle serait à réévaluer si le projet passait à plusieurs équipes
  autonomes.
