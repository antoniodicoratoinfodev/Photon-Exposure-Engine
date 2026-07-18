import * as React from 'react';
import { DataTable } from 'photon-exposure-ds';

export const ExposureMap = () => (
  <div style={{ padding: 18, minWidth: 380 }}>
    <DataTable
      columns={['f-number', 'Exposure time', '≈ Standard']}
      rows={[
        ['f/2.8', '1/500 s', '1/500'],
        ['f/4', '1/250 s', '1/250'],
        ['f/5.6', '1/125 s', '1/125'],
        ['f/8', '1/62 s', '1/60'],
        ['f/11', '1/31 s', '1/30'],
        ['f/16', '1/15 s', '1/15'],
      ]}
    />
  </div>
);
