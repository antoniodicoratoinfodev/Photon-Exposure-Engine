import * as React from 'react';
import { Card, Readout, ResultGlow } from 'photon-exposure-ds';

export const EVBlock = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <ResultGlow>
      <Readout>EV₁₀₀ = 12.3</Readout>
      <Readout tone="secondary" size="sm">
        EV (ISO 400) = 14.3
      </Readout>
    </ResultGlow>
  </div>
);

export const SavedTriad = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <Card variant="result">
      <ResultGlow>
        <Readout tone="value">f/5.6 · 1/125 s · ISO 400</Readout>
        <Readout tone="secondary" size="sm">
          ≈ standard 1/125
        </Readout>
        <Readout tone="gold" size="sm">
          Kodak Portra 400 · film time 1/100 s
        </Readout>
      </ResultGlow>
    </Card>
  </div>
);
