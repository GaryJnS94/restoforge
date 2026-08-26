# ADR-005 — État courant du stock plutôt que journal de mouvements

- **Statut** : accepté
- **Date** : 2026-08-26

## Contexte

Le module Stock doit suivre la quantité disponible de chaque produit. Deux
modèles sont envisageables :

- **État courant** : une colonne `stock` sur `product`, mise à jour à chaque
  évolution.
- **Journal de mouvements** : une table `stock_movement` enregistrant chaque
  entrée et sortie (quantité, date, origine) ; le stock courant est l'agrégat
  de ces mouvements.

Le journal est le modèle retenu par les logiciels d'inventaire établis. Il
impose en contrepartie une écriture par mouvement plutôt qu'une mise à jour,
une agrégation à chaque lecture du stock, et transforme la réception d'une
commande en insertion de N lignes suivie d'un recalcul.

Le périmètre MVP se limite à une boucle : le stock descend sous un seuil, une
commande fournisseur est passée, sa réception réapprovisionne. Ni inventaire
physique, ni audit, ni analyse de consommation n'en font partie.

## Décision

**Le stock est un état courant, porté par la colonne `product.stock`.**

Il est incrémenté au passage d'une commande en `RECEIVED`, dans la même
transaction que le changement de statut. Aucune table `stock_movement` n'est
créée.

## Conséquences

**Positives**

- La mise à jour est triviale — `product.stock += line.quantity` — et la
  lecture du stock est immédiate, sans agrégation.
- Le schéma se limite à quatre tables, cohérent avec le périmètre MVP.

**Négatives**

- Aucune traçabilité : un écart entre stock théorique et stock réel n'a pas
  d'explication consultable.
- Aucune reconstitution possible : un stock corrompu par un bug ne peut pas
  être recalculé.
- L'inventaire physique avec ajustement n'a aucun support dans le modèle.
- L'ajout ultérieur de la traçabilité impose une migration de schéma **et** une
  reprise de l'existant ; les mouvements antérieurs sont définitivement perdus.

Ce basculement, s'il devient nécessaire, fait l'objet d'un ADR remplaçant
celui-ci.
