# Design-sync notes — Photon Exposure Engine

Questo repo è un'app **Android/Java**: il design system web NON esiste nell'app — è una ricostruzione
fedele scritta apposta per claude.ai/design, in `design-system/` (React + TS, build esbuild+tsc).
Fonte della verità visiva: `app/src/main/res/values{,-night}/colors.xml`, `themes.xml`, i drawable
`bg_*.xml` e i layout. Se cambia la palette o lo stile dell'app, aggiornare **a mano**
`design-system/src/photon.css` (token `--photon-*`) e poi rilanciare il sync.

- Build del package: `npm --prefix design-system run build` (config `buildCmd`). Node 26, npm.
  `design-system/dist/` e `design-system/node_modules/` sono gitignorati.
- Entry per il converter: `--entry design-system/dist/index.js --node-modules design-system/node_modules`.
- Provider: `PhotonProvider` con `props: {fullscreen:false}` nelle card (a schermo intero gonfia le celle).
- Font: solo stack generici (`system-ui`, `ui-monospace`) — scelta deliberata, l'app usa Roboto/monospace
  di sistema; nessun `@font-face` da shippare, niente `[FONT_MISSING]` atteso.
- `cardMode: column` per SegmentedTabs, Select, TextArea, TextField (larghi, `[GRID_OVERFLOW]` altrimenti).
- Stati hover/focus non catturabili staticamente (focus ring oro dei campi, hover dei bottoni): non graded.
- Icone: il DS non esporta un set di icone; le preview usano SVG inline ~lucide-style con `currentColor`.
  `IconButton`/`SegmentedTabs` accettano qualsiasi SVG come children.
- Dialog/overlay: non esiste un componente Dialog; la superficie `bg_dialog` dell'app è coperta da `Card`
  (stesso raggio/stroke). Candidato per un'estensione futura.

## Known render warns

- (nessuno al close-out: render check 18/18 pulito, 0 bad/thin/variantsIdentical)

## Re-sync risks

- **Drift Android → web**: i token in `photon.css` sono una copia manuale di `colors.xml`; nessun check
  automatico li confronta. A ogni re-sync dopo modifiche UI dell'app, ri-diffare palette e raggi.
- Le preview inventano contenuti realistici (triadi, pellicole, date assolute "Jul 2026") — invecchiano
  ma non si rompono.
- Il build assume esbuild/tsc dai devDependencies del package (lockfile `design-system/package-lock.json`
  committato); Playwright/Chromium in cache utente (`chromium-1228`) per il render check.
- Primo sync completato il 2026-07-17 nel progetto `8594bbac-d2bc-49c5-96ae-0d1e09d10cd0`
  ("Photon Exposure Engine — Design System"); anchor `_ds_sync.json` caricato, quindi i re-sync
  partono diffando contro il remoto.
