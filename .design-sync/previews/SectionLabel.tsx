import * as React from 'react';
import { SectionLabel } from 'photon-exposure-ds';

export const Labels = () => (
  <div style={{ padding: 18, display: 'flex', flexDirection: 'column', gap: 14, minWidth: 280 }}>
    <SectionLabel>Scene input</SectionLabel>
    <SectionLabel>Film profile</SectionLabel>
    <SectionLabel>Exposure map</SectionLabel>
    <SectionLabel>Light · Exposure · Reciprocity</SectionLabel>
  </div>
);
