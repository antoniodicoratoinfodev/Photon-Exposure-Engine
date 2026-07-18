import * as React from 'react';
import { Select } from 'photon-exposure-ds';

const frame: React.CSSProperties = { padding: 18, minWidth: 340, display: 'flex', flexDirection: 'column', gap: 16 };

export const ISO = () => (
  <div style={frame}>
    <Select
      label="ISO sensitivity"
      defaultValue="400"
      options={['100', '200', '400', '800', '1600', '3200']}
    />
  </div>
);

export const FilmStock = () => (
  <div style={frame}>
    <Select
      label="Film stock (reciprocity data)"
      defaultValue="hp5"
      options={[
        { value: 'portra400', label: 'Kodak Portra 400' },
        { value: 'hp5', label: 'Ilford HP5+ 400' },
        { value: 'trix', label: 'Kodak Tri-X 400' },
        { value: 'velvia', label: 'Fujifilm Velvia 50' },
      ]}
    />
  </div>
);
