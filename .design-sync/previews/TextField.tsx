import * as React from 'react';
import { TextField } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, minWidth: 340, display: 'flex', flexDirection: 'column', gap: 16 };

export const Metering = () => (
  <div style={frame}>
    <TextField label="Metering (lux)" placeholder="e.g. 1250" suffix="lx" note="Flat sensor" inputMode="decimal" />
  </div>
);

export const Filled = () => (
  <div style={frame}>
    <TextField label="Metering (lux)" defaultValue="1250" suffix="lx" note="Flat sensor" inputMode="decimal" />
  </div>
);

export const Medium = () => (
  <div style={frame}>
    <TextField size="medium" label="Scene name" placeholder="Harbor at dusk" />
  </div>
);
