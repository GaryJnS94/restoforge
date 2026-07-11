# ADR-002 — GitLab principal + miroir GitHub

- **Statut** : accepté
- **Date** : 2026-07-11

## Contexte

Le projet a deux besoins qui ne pointent pas vers la même plateforme :

- **Industrialisation** : le développement s'appuie sur GitLab — CI/CD intégré,
  merge requests, et intégration native avec Linear pour l'automatisation du
  suivi des issues. C'est également la plateforme de référence de l'équipe.
- **Visibilité externe** : GitHub est la plateforme de référence pour
  l'exposition publique d'un projet ; un dépôt actif y sert de vitrine.

Trois options ont été envisagées : GitLab seul (pas de visibilité externe),
GitHub seul (CI/CD et intégration Linear moins alignées avec les pratiques
de l'équipe), ou les deux dépôts maintenus à la main (double push, risque
de divergence).

## Décision

GitLab est le dépôt principal (source de vérité : code, CI/CD, MR) ;
GitHub héberge un miroir public en lecture seule, synchronisé
automatiquement par la fonctionnalité de _repository mirroring_ de GitLab
(sens unique : GitLab → GitHub).

## Conséquences

**Positives :**

- Le meilleur des deux mondes : industrialisation complète côté GitLab,
  exposition publique côté GitHub, sans double maintenance.
- La synchronisation est automatique à chaque push : aucune commande
  supplémentaire, aucun risque d'oubli ou de divergence entre les dépôts.

**Négatives (assumées) :**

- Le miroir est en lecture seule : les issues et pull requests ouvertes sur
  GitHub ne seront pas traitées. Le README du miroir doit rediriger les
  contributions vers le dépôt GitLab.
- La synchronisation repose sur un jeton d'accès GitHub à durée de vie
  limitée : il devra être renouvelé à expiration, sous peine de miroir
  silencieusement gelé.
