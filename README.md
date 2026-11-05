# RestoForge

SaaS B2B de gestion d'approvisionnement pour la restauration : une suite
d'applications qui partagent un backend unique (Spring Boot 4 + PostgreSQL 16)
et un design system commun (forge-ui, Web Components Lit).

> Dépôt principal : GitLab (CI/CD) — miroir en lecture sur GitHub.

| App         | Techno           | Rôle                     | Statut        |
| ----------- | ---------------- | ------------------------ | ------------- |
| StockForge  | Angular 22 (PWA) | Catalogue & Stock        | MVP           |
| SupplyForge | React Native     | Commandes fournisseurs   | MVP (Phase 3) |
| MenuForge   | Angular (PWA)    | Prise de commande client | Hors-MVP      |

| Dossier     | Contenu                                  |
| ----------- | ---------------------------------------- |
| `api/`      | Backend Spring Boot 4 + PostgreSQL       |
| `web/`      | StockForge (Angular 22)                  |
| `mobile/`   | SupplyForge (React Native)               |
| `forge-ui/` | Design system (Lit)                      |
| `docs/`     | Documentation du projet (Docusaurus)     |

La documentation fonctionnelle et technique (règles métier, architecture,
guides, lexique) vit dans `docs/`. Ce fichier ne contient que les commandes.

## Prérequis

- **Docker Engine** et **Docker Compose v2**, utilisables sans `sudo` :
  `docker run --rm hello-world` doit fonctionner.
- **JDK 21** (version de référence de l'image et de la CI). Maven n'est pas
  nécessaire : le wrapper `./mvnw` est fourni dans `api/`.
- **Node.js 20 ou plus récent**, uniquement pour la documentation.

## Première installation

```bash
cp .env.example .env
# éditer .env : remplacer la valeur de POSTGRES_PASSWORD
```

Le fichier `.env` n'est pas versionné : il est à recréer sur chaque poste.

## Je veux…

Toutes les commandes partent de la **racine du dépôt**, sauf mention contraire.

### …lancer les tests du backend

```bash
cd api
./mvnw test
```

Les tests de repository démarrent un PostgreSQL éphémère (Testcontainers) :
le démon Docker doit tourner. La base du compose n'est pas utilisée.

### …lancer l'API avec les données de démonstration

C'est le mode de travail courant : l'API tourne depuis Maven, la base dans
Docker, et le profil `dev` ajoute le jeu de données de démonstration.

```bash
docker compose up -d postgres   # la base
docker compose stop api         # libère le port 8080 si le conteneur api tourne
cd api
(set -a && source ../.env && set +a && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev)
```

L'API est prête quand la console affiche `Started ApiApplication`. Elle reste
au premier plan : **Ctrl+C** l'arrête. Après une modification du code, il faut
l'arrêter et la relancer.

Dans un second terminal :

```bash
curl localhost:8080/products        # liste des produits
curl -i localhost:8080/products/999 # 404 au format ProblemDetail
```

> Maven ne lit pas `.env`. Le sous-shell `( … )` charge ses variables pour
> cette seule commande, sans les laisser dans le terminal.

### …lancer toute la pile en conteneurs

Pour vérifier que l'image Docker de l'API fonctionne, telle qu'elle sera
déployée :

```bash
docker compose up -d --build
docker compose ps                       # attendre l'état « healthy » de postgres
curl localhost:8080/actuator/health     # → {"status":"UP", …}
docker compose logs -f api              # suivre les logs (Ctrl+C pour quitter)
```

Sans `--build`, c'est l'ancienne image qui tourne, sans le code récent. Le
conteneur `api` ne déclare aucun profil : la base ne reçoit pas les données
de démonstration.

### …arrêter l'environnement

```bash
docker compose down
```

Les données sont conservées dans le volume Docker `db_data`.

### …repartir d'une base vide

```bash
docker compose down -v    # ⚠️ supprime le volume : toutes les données locales sont perdues
```

Relancer ensuite l'API avec le profil `dev` pour recréer le schéma et les
données de démonstration. C'est la réponse à l'erreur Flyway
`Detected resolved migration not applied to database`, qui apparaît après
l'ajout d'une migration de schéma sur une base ayant déjà reçu le jeu de
démonstration (`V100`).

### …me connecter à la base

```bash
docker exec -it restoforge-postgres psql -U restoforge -d restoforge
```

### …lancer la documentation

```bash
cd docs
npm ci          # jamais npm install : il réécrit package-lock.json
npm start       # → http://localhost:3000
npm run build   # vérifier que le site compile, avant chaque merge touchant docs/
```

Ne jamais lancer npm à la racine du dépôt : elle n'a pas de `package.json`.

## En cas de problème

| Symptôme | Cause probable | Réponse |
| --- | --- | --- |
| `./mvnw test` échoue en quelques secondes | Démon Docker arrêté ou utilisateur hors du groupe `docker` | `systemctl is-active docker`, puis `docker run --rm hello-world` |
| `password authentication failed` | `.env` non chargé par Maven | Lancer l'API avec le sous-shell `(set -a && source ../.env && set +a && …)` |
| `Port 8080 already in use` | Le conteneur `api` tourne déjà | `docker compose stop api` |
| `Detected resolved migration not applied to database` | Nouvelle migration sur une base ayant reçu `V100` | `docker compose down -v`, puis relancer avec le profil `dev` |
| `Failed to load ApplicationContext` | La vraie cause est plus bas | Depuis `api/` : `grep -n "Caused by" target/surefire-reports/*.txt \| tail -3` |

Procédures détaillées : `docs/docs/guides/environnement-local.md`.

## Contribuer

Le suivi se fait dans Linear (équipe RES). Branches, commits et merge
requests : voir le guide `docs/docs/guides/workflow.md`.
