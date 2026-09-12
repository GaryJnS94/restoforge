# ADR-006 — Découpage du code backend par domaine

- **Statut** : accepté
- **Date** : 2026-09-12

## Contexte

Le backend Spring Boot ne contenait jusqu'ici qu'une seule classe,
`ApiApplication`. La question de son organisation interne ne se posait donc pas.
RES-10 y introduit la première
[tranche verticale](../reference/lexique.md#tranche-verticale) — entité,
[repository](../reference/lexique.md#repository),
[service](../reference/lexique.md#service), controller — et fixe par là même le
moule de tout le code backend à venir : les classes suivantes se rangeront là où
les premières ont été rangées.

Deux organisations classiques s'opposent.

**Par couche technique.** Les packages portent le rôle des classes qu'ils
contiennent : `entity/`, `repository/`, `service/`, `controller/`. C'est le
découpage le plus répandu dans les tutoriels Spring, et le plus immédiat à
mettre en place.

**Par domaine métier.** Les packages portent les modules fonctionnels :
`stock/`, `commandes/`. Chacun rassemble toutes les couches du module dont il
porte le nom.

## Décision

**Le code backend est découpé par domaine, dans `com.restoforge.api` : les
packages `stock` et `commandes`.**

Ces deux packages correspondent aux deux modules du périmètre MVP et aux labels
Linear `module:stock` et `module:commandes`. Chaque package contient ses
entités, son repository, son service, son controller et ses
[DTO](../reference/lexique.md#dto-data-transfer-object), ces derniers dans un
sous-package `dto`.

**Justification**, par ordre d'importance.

1. **Ce qui change ensemble vit ensemble.** Une règle métier sur le produit
   touche simultanément l'entité, le service et le controller. Le découpage par
   domaine les réunit dans un même dossier ; le découpage par couche impose de
   naviguer entre quatre dossiers pour une modification unique.
2. **Les frontières de modules deviennent visibles dans l'arborescence.** Ouvrir
   `com.restoforge.api` donne la carte du périmètre. Le découpage par couche les
   dissout : un package `entity/` mélangerait les produits et les commandes, et
   plus rien n'indiquerait où s'arrête un module.
3. **Le couplage entre modules devient observable.** Un package `commandes` qui
   importe massivement `stock` est un signal lisible, repérable à la lecture des
   imports. Réparti sur quatre packages techniques, le même couplage ne se voit
   plus.
4. **L'ajout d'un futur module est une addition, pas une réorganisation.**
   MenuForge sera un package supplémentaire à côté des deux autres, là où le
   découpage par couche demanderait de refragmenter chacun des dossiers
   existants.

**Nommage.** Les packages portent le vocabulaire métier du projet — `stock`,
`commandes` — cohérent avec le [lexique](../reference/lexique.md) et avec les
labels Linear. Les classes, elles, restent en anglais : `Product`, `Supplier`,
`SupplierOrder`. L'entité ne porte aucun suffixe, parce qu'elle *est* le
concept : `Product` désigne le produit, là où `ProductRepository` et
`ProductService` désignent un rôle technique à son sujet.

## Conséquences

**Positives**

- Une évolution fonctionnelle se lit et se modifie dans un seul dossier.
- L'arborescence du code reflète le périmètre fonctionnel, et les deux restent
  comparables sans effort de traduction.
- Le couplage entre modules est visible à la lecture, donc discutable.

**Négatives**

- Sur un module réduit à une seule entité, le découpage par couche serait plus
  immédiat : l'intérêt du découpage par domaine ne se manifeste qu'à mesure que
  les modules grossissent.
- La convention doit être tenue à chaque nouvelle classe. Rien dans l'outillage
  ne l'impose, et une classe rangée au mauvais endroit rend le découpage moins
  lisible que pas de découpage du tout.
- Les classes réellement transverses — configuration, gestion globale des
  exceptions — n'appartiennent à aucun domaine et devront trouver leur place.
  Le besoin ne s'est pas encore présenté ; il fera l'objet d'une décision
  ultérieure le jour où il se présentera.
