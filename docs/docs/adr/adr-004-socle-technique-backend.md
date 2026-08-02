# ADR-004 — Socle technique du backend

- **Statut** : accepté
- **Date** : 2026-08-02

## Contexte

Le bootstrap de l'API (RES-8) imposait trois choix simultanés : la version de
Spring Boot, la version de Java, et la stratégie de base de données pour les
tests d'intégration.

**Version de Spring Boot.** La stack initiale du projet mentionnait
« Spring Boot 3.x ». Or la branche 3.x, dont la dernière version mineure est
la 3.5, arrive en fin de fenêtre de support open source. Spring Boot ne
désigne aucune version comme LTS : chaque mineure bénéficie d'environ douze
mois de correctifs, avec une nouvelle mineure tous les six mois. Démarrer un
projet neuf sur une branche non maintenue imposerait une migration avant même
la livraison du MVP.

**Version de Java.** Spring Boot 4.1 exige Java 17 au minimum et supporte
jusqu'à Java 26. Trois LTS étaient donc éligibles : 17, 21 et 25.

**Base de données de test.** Le smoke test généré au bootstrap
(`@SpringBootTest`) charge le contexte Spring complet, datasource incluse. Sans
configuration dédiée, il se connecte à la base de développement locale : le
test échoue en intégration continue, faute d'environnement, et peut altérer
les données de développement dès que des cas de test écriront en base. Deux
options se présentaient : une base embarquée H2 en mémoire, ou un conteneur
PostgreSQL éphémère via Testcontainers.

## Décision

**Spring Boot 4.1 sur Java 21 (LTS), avec Testcontainers pour les tests
d'intégration.**

Spring Boot 4.1 est la version mineure la plus récente de la branche 4.x, seule
branche actuellement sous support open source.

Java 21 est retenu plutôt que Java 25 : c'est le LTS le plus largement déployé,
il est déjà installé sur l'environnement de développement, et aucune
fonctionnalité du périmètre MVP — CRUD, REST, JPA — ne dépend de ce qui
distingue les deux versions. La décision est peu coûteuse à réviser : une
propriété du `pom.xml` et l'installation d'un JDK.

Testcontainers est retenu plutôt que H2 : le schéma relationnel constitue le
cœur du projet et sera exprimé en migrations Flyway comportant des contraintes
`CHECK`, des énumérations et des clés étrangères. Valider ces migrations contre
un moteur différent de celui exécuté en production ne prouverait rien. Le
conteneur de test utilise la même image que l'environnement local
(`postgres:16-alpine`) ; l'annotation `@ServiceConnection` injecte l'URL réelle
du conteneur — dont le port est attribué dynamiquement — avant la construction
de la datasource.

## Conséquences

**Positives**

- Le backend démarre sur une base maintenue, sans dette de migration immédiate.
- Le passage de Spring Boot 3 à 4 n'implique pas la réécriture `javax` vers
  `jakarta`, qui avait rendu coûteuse la migration de 2.7 vers 3.0.
- Les tests s'exécutent contre PostgreSQL 16, dans les mêmes conditions que la
  production. Les migrations Flyway sont rejouées à chaque exécution, ce qui
  les valide en continu.
- Les tests deviennent reproductibles et indépendants de la machine : chaque
  exécution repart d'une base vierge, sans dépendance à un environnement local
  ni risque d'altérer les données de développement.

**Négatives**

- La majorité de la documentation et des tutoriels Spring Boot disponibles
  cible la branche 3.x. Points de divergence à surveiller : Jackson 3.x,
  JUnit 6, et certains noms de starters modifiés par la modularisation
  introduite en 4.x.
- Testcontainers ajoute deux dépendances de test et exige un démon Docker
  disponible sur toute machine exécutant la suite de tests, environnement
  d'intégration continue compris.
- Le démarrage du conteneur allonge chaque exécution de tests de quelques
  secondes. Coût négligeable au volume actuel, à réévaluer si la suite
  s'étoffe.
- L'image du conteneur de test doit rester alignée sur celle de `compose.yaml`.
  Une divergence entre les deux ferait valider le schéma contre un moteur qui
  n'est exécuté nulle part.
