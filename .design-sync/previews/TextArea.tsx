import * as React from 'react';
import { TextArea } from 'photon-exposure-ds';

export const Notes = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <TextArea label="Notes" placeholder="Where, light conditions, subject…" />
  </div>
);

export const Filled = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <TextArea
      label="Notes"
      defaultValue="Backlit portrait by the harbor — metered on the face, sky clipped one stop."
    />
  </div>
);
