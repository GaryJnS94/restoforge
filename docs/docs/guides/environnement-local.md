---
sidebar_position: 2
title: Environnement de dev local
---

# Environnement de dev local (Docker)

L'environnement de développement repose entièrement sur Docker : chaque brique
(base de données, et bientôt l'API) tourne dans un conteneur, orchestré par
`docker-compose.dev.yml` à la racine du dépôt. Objectif : tout lancer d'une
seule commande, sur n'importe quelle machine, sans rien installer d'autre que Docker.

À ce stade, l'environnement contient un seul service : **PostgreSQL 16**.
L'API Spring Boot le rejoindra (voir RES-8).

## Démarrage

```bash
cp .env.example .env
# éditer .env : définir un vrai POSTGRES_PASSWORD

docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.dev.yml ps   # attendre le statut (healthy)
```

## Les fichiers d'environnement

| Fichier        | Versionné | Rôle                                            |
| -------------- | --------- | ----------------------------------------------- |
| `.env.example` | ✅ oui    | Le contrat : liste des variables, valeurs bidon |
| `.env`         | ❌ non    | Les vraies valeurs (secrets) — ignoré par Git   |

Docker Compose lit automatiquement le fichier `.env` situé à côté du fichier
compose et substitue les variables `${...}`.

:::caution Initialisation unique
PostgreSQL ne lit `POSTGRES_USER` / `POSTGRES_PASSWORD` / `POSTGRES_DB`
**qu'au premier démarrage sur un volume vierge**. Les identifiants sont ensuite
stockés dans le volume : modifier le `.env` après coup ne change PAS le mot de
passe de la base. Pour réinitialiser, il faut détruire le volume (voir plus bas).
:::

## Persistance des données

Les données vivent dans le volume nommé `restoforge_db_data`, géré par Docker
**à l'extérieur du conteneur** (monté sur `/var/lib/postgresql/data`).
Conséquence : le conteneur est jetable, les données ne le sont pas.

```bash
docker compose -f docker-compose.dev.yml down   # détruit le conteneur, GARDE les données
docker volume ls                                # restoforge_db_data est toujours là
```

:::danger down -v : destructeur
`docker compose -f docker-compose.dev.yml down -v` supprime **aussi le volume,
donc toutes les données**. À réserver aux réinitialisations volontaires
(changement d'identifiants, base corrompue, repartir de zéro).
:::

## Se connecter à la base

Via le conteneur, sans installer psql sur le poste :

```bash
docker exec -it restoforge-postgres psql -U restoforge -d restoforge
```

Commandes utiles dans le prompt psql : `\l` (bases), `\dt` (tables), `\q` (quitter).

## Healthcheck

Le service déclare un `healthcheck` basé sur `pg_isready` : Docker vérifie
toutes les 5 s que la base accepte réellement les connexions, et expose le
résultat dans la colonne STATUS du `ps` (`healthy` / `unhealthy`).

Ce n'est pas cosmétique : quand l'API rejoindra le compose (RES-8), elle
déclarera `depends_on: postgres: condition: service_healthy` — elle ne
démarrera qu'une fois la base réellement prête, pas juste le conteneur lancé.

## Diagnostic

En cas de doute sur le fichier compose ou les variables :

```bash
docker compose -f docker-compose.dev.yml config
```

Cette commande valide et affiche la configuration résolue (variables
substituées) **sans rien lancer**. Si des `WARN variable is not set`
apparaissent, le `.env` est absent ou mal placé.
