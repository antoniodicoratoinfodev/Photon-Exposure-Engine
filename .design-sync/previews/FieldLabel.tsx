import * as React from 'react';
import { FieldLabel } from 'photon-exposure-ds';

export const Labels = () => (
  <div style={{ padding: 18, display: 'flex', flexDirection: 'column', gap: 12, minWidth: 280 }}>
    <FieldLabel>Metering (lux)</FieldLabel>
    <FieldLabel>ISO sensitivity</FieldLabel>
    <FieldLabel>Film stock (reciprocity data)</FieldLabel>
  </div>
);
