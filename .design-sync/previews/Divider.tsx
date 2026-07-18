import * as React from 'react';
import { Card, Divider, FieldLabel, Readout } from 'photon-exposure-ds';

export const InCard = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <Card>
      <FieldLabel>Metering (lux)</FieldLabel>
      <Divider gap={18} />
      <FieldLabel>ISO sensitivity</FieldLabel>
    </Card>
  </div>
);

export const BetweenReadouts = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <Card variant="result">
      <Readout>EV₁₀₀ = 12.3</Readout>
      <Divider gap={16} />
      <Readout tone="value" size="lg">
        Aperture: f/5.6
      </Readout>
    </Card>
  </div>
);
