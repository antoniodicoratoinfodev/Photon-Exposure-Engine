import * as React from 'react';
import { Card, PhotonProvider, Readout, SectionLabel } from 'photon-exposure-ds';

const inner = (
  <div style={{ padding: 18, display: 'flex', flexDirection: 'column', gap: 12, minWidth: 340 }}>
    <SectionLabel>Scene input</SectionLabel>
    <Card>
      <Readout tone="value" size="lg">
        Aperture: f/5.6
      </Readout>
      <Readout tone="secondary" size="sm">
        EV (ISO 400) = 14.3
      </Readout>
    </Card>
  </div>
);

export const Dark = () => <PhotonProvider fullscreen={false}>{inner}</PhotonProvider>;

export const Light = () => (
  <PhotonProvider theme="light" fullscreen={false}>
    {inner}
  </PhotonProvider>
);
