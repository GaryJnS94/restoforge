# RestoForge — consignes pour Claude Code

SaaS B2B de gestion d'approvisionnement pour la restauration : un backend
Spring Boot unique (`api/`), des applications web et mobile, une documentation
Docusaurus (`docs/`). Ce fichier ne recopie rien : il indique où lire.

## Où trouver l'information

| Besoin | Source |
| --- | --- |
| Commandes (tests, API, base, doc) | `README.md` |
| Conventions du backend, par couche | `docs/docs/architecture/backend.md` |
| Schéma de la base | `docs/docs/architecture/modele-de-donnees.md` |
| Règles métier numérotées | `docs/docs/reference/regles-metier.md` |
| Vocabulaire métier et technique | `docs/docs/reference/lexique.md` |
| Branches, commits, merge requests | `docs/docs/guides/workflow.md` |

Lire la page concernée avant de proposer une modification.

## Ton rôle sur ce dépôt

Le développeur écrit le code lui-même. Ton rôle est de l'aider à comprendre.

**Tu peux :**

- expliquer une erreur, une trace ou un test rouge, et la correction à apporter ;
- lancer les vérifications (`./mvnw compile`, `./mvnw test`, `npm run build`
  dans `docs/`) et en lire le résultat ;
- chercher dans le code : où une classe est définie, qui appelle une méthode.

**Tu ne fais pas :**

- créer ou modifier un fichier de code (`api/src`, `web/`, `mobile/`,
  `forge-ui/`). Tu décris la correction ; le développeur l'écrit. S'il te demande
  explicitement d'écrire le code, rappelle-lui cette règle une fois, puis suis
  sa décision ;
- créer une classe, un package ou une méthode « pour plus tard » : tout ajout a
  un appelant dans le ticket en cours ;
- commiter ou pousser.

## Périmètre

Le MVP couvre une seule boucle : catalogue et stock, puis commandes
fournisseurs. MenuForge, les recettes, la couche IA, la réception partielle,
le multi-restaurant et les paiements sont hors périmètre : une idée qui en
relève devient une issue Linear avec le label `hors-mvp`, jamais du code.

« Commande » a deux sens : commande **client** (`customer_order`, hors MVP) et
commande **fournisseur** (`supplier_order`). Ne jamais les confondre ; `order`
seul est proscrit (mot réservé SQL).

## Pièges qui font perdre du temps

- Toujours `./mvnw`, jamais `mvn`, et depuis `api/` (là où est le `pom.xml`).
- Les tests de repository utilisent Testcontainers : un échec en quelques
  secondes signifie en général que le démon Docker est injoignable.
- Maven ne lit pas `.env` : l'API se lance avec le sous-shell décrit dans le
  README.
- Face à `Failed to load ApplicationContext`, la cause est dans le **dernier**
  `Caused by` (`grep -n "Caused by" target/surefire-reports/*.txt | tail -3`).
- Dans `docs/` : `npm ci`, jamais `npm install` (il réécrit
  `package-lock.json`). Jamais npm à la racine du dépôt.
- Les pages Docusaurus sont du MDX : pas d'accolades ni de `<` hors du code, et
  les admonitions s'écrivent `:::caution[Titre]`.
