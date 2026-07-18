import * as React from 'react';
import { IconButton, PageHeader } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, minWidth: 360 };

const ThemeIcon = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
  </svg>
);

const BookmarkIcon = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M19 21l-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2Z" />
  </svg>
);

const BackIcon = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M19 12H5" />
    <path d="m12 19-7-7 7-7" />
  </svg>
);

export const AppHeader = () => (
  <div style={frame}>
    <PageHeader
      eyebrow="Light · Exposure · Reciprocity"
      title="Photon Exposure Engine"
      subtitle="A precise exposure workspace for digital and film"
      leading={
        <IconButton label="Toggle theme">
          <ThemeIcon />
        </IconButton>
      }
      trailing={
        <IconButton label="Saved exposures">
          <BookmarkIcon />
        </IconButton>
      }
    />
  </div>
);

export const SubPage = () => (
  <div style={frame}>
    <PageHeader
      eyebrow="Reference"
      title="Formulas &amp; theory"
      subtitle="The math behind the meter"
      leading={
        <IconButton label="Back">
          <BackIcon />
        </IconButton>
      }
    />
  </div>
);

export const TitleOnly = () => (
  <div style={frame}>
    <PageHeader title="Saved exposures" subtitle="12 entries" />
  </div>
);
