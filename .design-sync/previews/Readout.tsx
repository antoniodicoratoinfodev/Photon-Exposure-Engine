import * as React from 'react';
import { Readout } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, display: 'flex', flexDirection: 'column', gap: 10, minWidth: 320 };

export const Tones = () => (
  <div style={frame}>
    <Readout>EV₁₀₀ = 12.3</Readout>
    <Readout tone="secondary" size="sm">
      EV (ISO 400) = 14.3
    </Readout>
    <Readout tone="value" size="lg">
      Aperture: f/5.6
    </Readout>
    <Readout tone="value" size="lg">
      Shutter speed: 1/125 s
    </Readout>
    <Readout tone="gold" size="lg">
      Film time: 2.4 s
    </Readout>
  </div>
);

export const Sizes = () => (
  <div style={frame}>
    <Readout size="sm">1/125 s — small 13px</Readout>
    <Readout size="md">1/125 s — medium 15px</Readout>
    <Readout size="lg">1/125 s — large 18px</Readout>
  </div>
);
