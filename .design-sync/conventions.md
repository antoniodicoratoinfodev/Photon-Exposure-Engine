# Photon Exposure Engine — build conventions

**Wrap every screen in `PhotonProvider`.** The design tokens, base font, text color and page background live on it — components rendered outside it sit on a transparent background with inherited fonts. Dark is the default theme (the app's native look); pass `theme="light"` for the light palette. It fills the viewport by default; use `fullscreen={false}` for embedded fragments.

```tsx
import { PhotonProvider, PageHeader, SectionLabel, Card, TextField, Button } from 'photon-exposure-ds';

<PhotonProvider>
  <div style={{ maxWidth: 430, margin: '0 auto', padding: 18, display: 'flex', flexDirection: 'column', gap: 12 }}>
    <PageHeader eyebrow="Light · Exposure · Reciprocity" title="Photon Exposure Engine" subtitle="A precise exposure workspace" />
    <Card>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
        <SectionLabel>Scene input</SectionLabel>
        <TextField label="Metering (lux)" placeholder="e.g. 1250" suffix="lx" note="Flat sensor" inputMode="decimal" />
        <Button>Calculate exposure time</Button>
      </div>
    </Card>
  </div>
</PhotonProvider>
```

**Styling idiom: props on components, CSS variables for glue.** There are no utility classes — never invent class names. Components carry the design language through props (`variant`, `tone`, `size`). For your own layout wrappers use inline flex/grid styles and the shipped tokens via `var(--…)`:

- Surfaces: `--photon-bg`, `--photon-bg-elevated`, `--photon-card`, `--photon-card-result`, `--photon-field`, `--photon-row-alt`, `--photon-header-row`
- Strokes: `--photon-stroke`, `--photon-stroke-strong`, `--photon-stroke-result`, `--photon-divider`
- Text: `--photon-text`, `--photon-text-2`, `--photon-text-3`, `--photon-text-on-accent`
- Accents: `--photon-gold`, `--photon-gold-soft`, `--photon-value`, `--photon-orange`, `--photon-on-orange`, `--photon-glow-start`, `--photon-glow-end`
- Radii: `--photon-r-card` (22px), `--photon-r-card-result` (24px), `--photon-r-field` (14px), `--photon-r-badge`, `--photon-r-glow`, `--photon-r-track`, `--photon-r-indicator`
- Fonts: `--photon-font-sans`, `--photon-font-mono`

**Layout DNA of the app.** Single-column phone layout (~430px wide): a vertical stack of `Card`s with 12px gaps and 18px page padding. Each card opens with a `SectionLabel`; inputs get their label through the `label` prop of `TextField` / `Select` / `TextArea` (or a standalone `FieldLabel`); the card's main action is a full-width gold `Button` at the bottom, secondary navigation uses `variant="outlined"`. Every computed measurement renders in monospace via `Readout` — tone `value` for aperture/shutter lines, `gold` for film-corrected times — inside a `Card variant="result"`, with headline EV numbers in a `ResultGlow` panel and a `Badge` ("Metered") in the title row. Mode switches are `SegmentedTabs`; screens are topped by `PageHeader` with `IconButton`s in its `leading`/`trailing` slots; tabular data (the exposure map) uses `DataTable`, formulas use `FormulaCode`, saved entries use `ListItem`.

**Where the truth lives.** Before styling, read `styles.css` and `_ds_bundle.css` (every token above and all `photon-*` classes are defined there), and each component's API in `components/general/<Name>/<Name>.d.ts` with usage notes in the sibling `.prompt.md`.
