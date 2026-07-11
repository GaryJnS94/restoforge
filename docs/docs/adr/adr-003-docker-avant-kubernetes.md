# ADR-003 — Docker d'abord, Kubernetes ensuite

- **Statut** : accepté
- **Date** : 2026-07-11

## Contexte

Le MVP doit pouvoir être lancé en local d'une seule commande (API,
base de données, front) et être déployable sur un serveur unique.

Deux niveaux d'outillage de conteneurisation existent :

- **Docker + docker-compose** : construction des images et orchestration
  simple sur un hôte unique. Mise en œuvre rapide, adaptée à un
  déploiement mono-serveur.
- **Kubernetes** : orchestration complète (scaling, self-healing,
  rolling updates), mais complexité opérationnelle significative.

Kubernetes représente un investissement important (courbe d'apprentissage,
exploitation) qui n'est pas justifié pour un déploiement mono-serveur ;
il reste pertinent comme évolution ultérieure de l'infrastructure. La
question n'est donc pas le choix final, mais l'ordre d'adoption.

## Décision

Le MVP est conteneurisé et orchestré avec Docker + docker-compose
(local et déploiement) ; Kubernetes est explicitement reporté après la
livraison du MVP, comme évolution d'infrastructure documentée.

## Conséquences

**Positives :**

- La livraison du MVP ne dépend pas de la maîtrise de Kubernetes :
  aucun risque que l'infrastructure bloque la boucle métier.
- Progression technique cohérente : Kubernetes orchestre des
  conteneurs — la construction d'images propres (Dockerfiles, réseaux,
  volumes) est un prérequis à leur orchestration.
- docker-compose couvre fonctionnellement le besoin d'un déploiement
  mono-serveur, cible du MVP.

**Négatives (assumées) :**

- Le passage à Kubernetes impliquera une réécriture de la couche
  d'orchestration : le docker-compose.yml devra être converti en
  manifests Kubernetes (Deployments, Services, ConfigMaps), ce qui
  n'est pas une traduction automatique.
- D'ici là, pas de scaling automatique ni de self-healing : acceptable
  pour un MVP
