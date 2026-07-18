import * as React from 'react';
import { FormulaCode } from 'photon-exposure-ds';

export const EV = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <FormulaCode>{'EV = log₂(N² / t)\nE  = 2.5 · 2^EV₁₀₀   [lux]'}</FormulaCode>
  </div>
);

export const Reciprocity = () => (
  <div style={{ padding: 18, minWidth: 340 }}>
    <FormulaCode>{'t_corrected = t_metered^p\np = film reciprocity exponent (t > 1 s)'}</FormulaCode>
  </div>
);
