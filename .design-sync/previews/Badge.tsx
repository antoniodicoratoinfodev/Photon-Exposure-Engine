import * as React from 'react';
import { Badge } from 'photon-exposure-ds';

export const Modes = () => (
  <div style={{ padding: 18, display: 'flex', gap: 12, alignItems: 'center' }}>
    <Badge variant="mode">A</Badge>
    <Badge variant="mode">B</Badge>
    <Badge variant="mode">12</Badge>
  </div>
);

export const Status = () => (
  <div style={{ padding: 18, display: 'flex', gap: 12, alignItems: 'center' }}>
    <Badge>Metered</Badge>
    <Badge>Reciprocity</Badge>
  </div>
);
