import * as React from 'react';

export interface SectionLabelProps {
  children?: React.ReactNode;
}

/**
 * Gold uppercase eyebrow that opens every card section
 * ("SCENE INPUT", "FILM PROFILE", "EXPOSURE MAP").
 */
export function SectionLabel({ children }: SectionLabelProps) {
  return <div className="photon-section-label">{children}</div>;
}

export interface FieldLabelProps {
  children?: React.ReactNode;
}

/** Small bold label placed above an input control ("Metering (lux)", "ISO sensitivity"). */
export function FieldLabel({ children }: FieldLabelProps) {
  return <div className="photon-field-label">{children}</div>;
}

export interface ReadoutProps {
  /** Color role: primary/secondary body text, `value` (light gold, bold) for computed numbers, `gold` for film-corrected results. */
  tone?: 'primary' | 'secondary' | 'value' | 'gold';
  /** Text size: sm 13px, md 15px, lg 18px. */
  size?: 'sm' | 'md' | 'lg';
  children?: React.ReactNode;
}

/**
 * Monospace measurement line for computed values —
 * "EV₁₀₀ = 12.3", "Aperture: f/5.6", "Shutter speed: 1/125 s".
 */
export function Readout({ tone = 'primary', size = 'md', children }: ReadoutProps) {
  return <div className={`photon-readout photon-readout--${tone} photon-readout--${size}`}>{children}</div>;
}

export interface DividerProps {
  /** Vertical margin in px around the rule. Defaults to 0 — parent gap usually handles spacing. */
  gap?: number;
}

/** 1px horizontal rule between card sections. */
export function Divider({ gap = 0 }: DividerProps) {
  return <hr className="photon-divider" style={gap ? { margin: `${gap}px 0` } : undefined} />;
}
