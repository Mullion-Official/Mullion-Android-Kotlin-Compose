# MULLION SDK docs site

Built from the `dunwu/gitbook-templates` **doc-template** layout
(`book.json` + `docs/SUMMARY.md` + `docs/`), rendered by **HonKit** — the
maintained GitBook 3-compatible toolchain.

```bash
cd gitbook
npm install
npm run build      # sync guides/images, regenerate API docs, honkit build
npx honkit serve   # local preview at http://localhost:4000
```

- `docs/README.md` — landing page
- `docs/SUMMARY.md` — navigation
- `docs/design-style.md` — design system
- `docs/api/*.md` — generated SDK reference (`scripts/gen_api_docs.py`)
- `scripts/sync_guides.sh` — syncs `docs/*.md` + images, then regenerates the API

GitHub Pages deploys `gitbook/_book` via `.github/workflows/pages.yml`.
