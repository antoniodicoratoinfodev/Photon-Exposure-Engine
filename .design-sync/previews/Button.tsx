import * as React from 'react';
import { Button } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, display: 'flex', flexDirection: 'column', gap: 12, minWidth: 320 };

export const Primary = () => (
  <div style={frame}>
    <Button>Calculate exposure time</Button>
  </div>
);

export const Outlined = () => (
  <div style={frame}>
    <Button variant="outlined">Formulas &amp; theory</Button>
  </div>
);

export const Disabled = () => (
  <div style={frame}>
    <Button disabled>Calculate aperture</Button>
    <Button variant="outlined" disabled>
      Formulas &amp; theory
    </Button>
  </div>
);

export const Inline = () => (
  <div style={{ ...frame, flexDirection: 'row' }}>
    <Button fullWidth={false}>Save exposure</Button>
    <Button fullWidth={false} variant="outlined">
      Cancel
    </Button>
  </div>
);
