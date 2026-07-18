import * as React from 'react';
import { SegmentedTabs } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, minWidth: 360 };

const DigitalIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3Z" />
    <circle cx="12" cy="13" r="3" />
  </svg>
);

const FilmIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="3" y="3" width="18" height="18" rx="2" />
    <path d="M7 3v18M17 3v18M3 8h4M3 16h4M17 8h4M17 16h4" />
  </svg>
);

export const DigitalAnalog = () => (
  <div style={frame}>
    <SegmentedTabs
      defaultValue="digital"
      items={[
        { value: 'digital', label: 'Digital', icon: <DigitalIcon /> },
        { value: 'analog', label: 'Analog', icon: <FilmIcon /> },
      ]}
    />
  </div>
);

export const SecondSelected = () => (
  <div style={frame}>
    <SegmentedTabs
      defaultValue="analog"
      items={[
        { value: 'digital', label: 'Digital', icon: <DigitalIcon /> },
        { value: 'analog', label: 'Analog', icon: <FilmIcon /> },
      ]}
    />
  </div>
);

export const TextOnly = () => (
  <div style={frame}>
    <SegmentedTabs
      defaultValue="aperture"
      items={[
        { value: 'aperture', label: 'Aperture' },
        { value: 'shutter', label: 'Shutter' },
        { value: 'manual', label: 'Manual' },
      ]}
    />
  </div>
);
