import * as React from 'react';
import { IconButton } from 'photon-exposure-ds';

const ThemeIcon = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
  </svg>
);

const SaveIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2Z" />
    <path d="M17 21v-8H7v8M7 3v5h8" />
  </svg>
);

const TrashIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M3 6h18" />
    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6" />
    <path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
  </svg>
);

export const Tones = () => (
  <div style={{ padding: 18, display: 'flex', gap: 12, alignItems: 'center' }}>
    <IconButton label="Toggle theme">
      <ThemeIcon />
    </IconButton>
    <IconButton label="Save exposure" tone="gold">
      <SaveIcon />
    </IconButton>
    <IconButton label="Delete entry" tone="danger">
      <TrashIcon />
    </IconButton>
  </div>
);

export const Sizes = () => (
  <div style={{ padding: 18, display: 'flex', gap: 12, alignItems: 'center' }}>
    <IconButton label="Toggle theme">
      <ThemeIcon />
    </IconButton>
    <IconButton label="Save exposure" size="sm" tone="gold">
      <SaveIcon />
    </IconButton>
  </div>
);
