# API — RestoForge

Backend unique de la suite RestoForge. Sert StockForge (web) et
SupplyForge (mobile) via un contrat OpenAPI commun.

## Stack

Spring Boot 4.1 · Java 21 (LTS) · PostgreSQL · Flyway · Maven

## Prérequis

- JDK 21 installé (`java -version`)
- Docker et Docker Compose
- Un fichier `.env` à la racine du monorepo, créé depuis `.env.example`

## Lancer en local

La base de données doit tourner avant l'API :

```bash
docker compose up -d
docker compose ps        # attendre le statut 'healthy'
```

Puis, depuis le dossier `api/` :

```bash
set -a && source ../.env && set +a
./mvnw spring-boot:run
```

### Pourquoi `set -a && source ../.env`

Docker Compose lit `.env` automatiquement ; Spring Boot non. Sans cette
étape, l'application démarre avec les valeurs par défaut d'
`application.yml` et échoue sur `password authentication failed`, car
elles ne correspondent pas aux identifiants avec lesquels le conteneur
PostgreSQL a été initialisé.

`set -a` exporte automatiquement toute variable définie ensuite,
`source` charge le fichier, `set +a` rétablit le comportement normal du
shell. Ces variables ne survivent qu'à la session shell courante : elles
sont à recharger dans chaque nouveau terminal.

### Pourquoi `./mvnw` et non `mvn`

Le wrapper fige la version de Maven utilisée par le projet et la
télécharge au besoin. Tous les environnements — poste local, CI —
construisent ainsi avec la même version, sans dépendre de ce qui est
installé sur la machine.

## Vérifier

```bash
curl -s localhost:8080/actuator/health
```

Réponse attendue : le statut `UP`, accompagné du détail du composant
`db` confirmant que la connexion à PostgreSQL est établie.

## Tester

```bash
./mvnw test
```

## Configuration

Variables d'environnement lues au démarrage, avec leur valeur par défaut
telle que définie dans `src/main/resources/application.yml` :

| Variable | Défaut | Rôle |
|---|---|---|
| `DB_HOST` | `localhost` | Hôte PostgreSQL |
| `DB_PORT` | `5432` | Port PostgreSQL |
| `POSTGRES_DB` | `restoforge` | Nom de la base |
| `POSTGRES_USER` | `restoforge` | Utilisateur de connexion |
| `POSTGRES_PASSWORD` | `changeme` | Mot de passe — à définir dans `.env`, jamais dans le dépôt |

`DB_HOST` et `DB_PORT` n'ont pas à être renseignés en local : les valeurs
par défaut pointent déjà vers le conteneur exposé par `compose.yaml`.

### Schéma de base de données

Le schéma est géré exclusivement par Flyway, via les migrations de
`src/main/resources/db/migration`. La configuration
`spring.jpa.hibernate.ddl-auto: validate` garantit qu'Hibernate ne crée
ni ne modifie jamais de table : il se contente de vérifier au démarrage
que les entités correspondent au schéma en place. Toute évolution du
modèle passe donc par une nouvelle migration Flyway.
