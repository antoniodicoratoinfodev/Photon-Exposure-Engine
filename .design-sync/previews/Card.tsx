import * as React from 'react';
import { Badge, Button, Card, Divider, FieldLabel, Readout, ResultGlow, SectionLabel } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, minWidth: 360, display: 'flex', flexDirection: 'column', gap: 12 };

export const Default = () => (
  <div style={frame}>
    <Card>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
        <SectionLabel>Scene input</SectionLabel>
        <FieldLabel>Metering (lux)</FieldLabel>
        <Readout tone="secondary" size="sm">
          Flat sensor · incident reading
        </Readout>
      </div>
    </Card>
  </div>
);

export const Accent = () => (
  <div style={frame}>
    <Card variant="accent">
      <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
        <SectionLabel>Film profile</SectionLabel>
        <Readout tone="secondary" size="sm">
          Ilford HP5+ · reciprocity correction active
        </Readout>
      </div>
    </Card>
  </div>
);

export const Result = () => (
  <div style={frame}>
    <Card variant="result">
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <span style={{ fontSize: 20, fontWeight: 500 }}>Results</span>
          <Badge>Metered</Badge>
        </div>
        <ResultGlow>
          <Readout>EV₁₀₀ = 12.3</Readout>
          <Readout tone="secondary" size="sm">
            EV (ISO 400) = 14.3
          </Readout>
        </ResultGlow>
        <Divider />
        <Readout tone="value" size="lg">
          Aperture: f/5.6
        </Readout>
        <Readout tone="value" size="lg">
          Shutter speed: 1/125 s
        </Readout>
        <Button>Save exposure</Button>
      </div>
    </Card>
  </div>
);
