# RestoForge — Documentation

Site de documentation vivante du projet RestoForge, construit avec
[Docusaurus](https://docusaurus.io/). Il centralise les décisions
d'architecture (ADR), les guides et la documentation des modules.

## Démarrage

```bash
npm install     # première fois uniquement
npm start       # serveur de dev sur http://localhost:3000
npm run build   # build de production (valide liens et MDX)
```

## Structure

- `docs/` — le contenu du site (chaque `.md` devient une page)
  - `docs/adr/` — les Architecture Decision Records ; pour en ajouter un,
    dupliquer `docs/adr/_template.md`
  - `docs/architecture/`, `docs/guides/`, `docs/modules/` — sections à venir
- `docusaurus.config.ts` — configuration du site (titre, navbar, footer)
- `src/` — page d'accueil et CSS custom
- `static/` — assets servis tels quels (images, favicon)

Pour les conventions et pièges (MDX, sidebar, liens), voir
[GUIDE-DOCUSAURUS.md](./GUIDE-DOCUSAURUS.md).
