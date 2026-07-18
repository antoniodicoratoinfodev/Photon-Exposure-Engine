import * as React from 'react';
import { IconButton, ListItem } from 'photon-exposure-ds';

const TrashIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M3 6h18" />
    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6" />
    <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
  </svg>
);

const frame: React.CSSProperties = { padding: 18, minWidth: 380, display: 'flex', flexDirection: 'column', gap: 10 };

export const SavedExposures = () => (
  <div style={frame}>
    <ListItem
      badge="1"
      title="f/5.6 · 1/125 s · ISO 400"
      subtitle="≈ standard 1/125"
      meta="17 Jul 2026 · 14:32 · Digital"
      trailing={
        <IconButton label="Delete entry" size="sm" tone="danger">
          <TrashIcon />
        </IconButton>
      }
      onClick={() => {}}
    />
    <ListItem
      badge="2"
      title="f/16 · 2.4 s · ISO 100"
      subtitle="metered 1.0 s"
      meta="16 Jul 2026 · 21:08 · Analog · Ilford HP5+"
      trailing={
        <IconButton label="Delete entry" size="sm" tone="danger">
          <TrashIcon />
        </IconButton>
      }
      onClick={() => {}}
    />
  </div>
);

export const WithNotes = () => (
  <div style={frame}>
    <ListItem
      badge="3"
      title="f/2.8 · 1/500 s · ISO 200"
      subtitle="≈ standard 1/500"
      meta="15 Jul 2026 · 09:15 · Digital"
      notes="Backlit portrait by the harbor — metered on the face, sky clipped one stop."
    />
  </div>
);
