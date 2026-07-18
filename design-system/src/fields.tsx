import * as React from 'react';
import { FieldLabel } from './typography';

export interface TextFieldProps {
  /** Renders a FieldLabel above the box. */
  label?: React.ReactNode;
  /** Gold unit suffix inside the box ("lx"). */
  suffix?: React.ReactNode;
  /** Monospace tertiary note under the box ("Flat sensor"). */
  note?: React.ReactNode;
  /** `large` — 64px / 26px text for the hero metering input; `medium` — 52px / 16px. */
  size?: 'large' | 'medium';
  placeholder?: string;
  value?: string;
  defaultValue?: string;
  onChange?: (value: string) => void;
  /** HTML inputmode hint; the metering field uses `decimal`. */
  inputMode?: 'text' | 'decimal' | 'numeric';
}

/**
 * Gold-stroked input on the field surface. The hero variant is the big
 * numeric metering field with its unit suffix.
 */
export function TextField({
  label,
  suffix,
  note,
  size = 'large',
  placeholder,
  value,
  defaultValue,
  onChange,
  inputMode,
}: TextFieldProps) {
  return (
    <label className={'photon-textfield' + (size === 'medium' ? ' photon-textfield--medium' : '')}>
      {label && <FieldLabel>{label}</FieldLabel>}
      <span className="photon-textfield__box">
        <input
          className="photon-textfield__input"
          placeholder={placeholder}
          value={value}
          defaultValue={defaultValue}
          inputMode={inputMode}
          onChange={(e) => onChange?.(e.target.value)}
        />
        {suffix && <span className="photon-textfield__suffix">{suffix}</span>}
      </span>
      {note && <span className="photon-textfield__note">{note}</span>}
    </label>
  );
}

export interface TextAreaProps {
  label?: React.ReactNode;
  placeholder?: string;
  value?: string;
  defaultValue?: string;
  onChange?: (value: string) => void;
  /** Minimum visible lines; defaults to 3 like the save-notes field. */
  rows?: number;
}

/** Multiline notes field on the field surface, focus ring in gold. */
export function TextArea({ label, placeholder, value, defaultValue, onChange, rows = 3 }: TextAreaProps) {
  return (
    <label className="photon-textarea">
      {label && <FieldLabel>{label}</FieldLabel>}
      <textarea
        className="photon-textarea__control"
        placeholder={placeholder}
        value={value}
        defaultValue={defaultValue}
        rows={rows}
        onChange={(e) => onChange?.(e.target.value)}
      />
    </label>
  );
}

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectProps {
  label?: React.ReactNode;
  /** Options as `{value, label}` pairs or plain strings. */
  options: Array<SelectOption | string>;
  value?: string;
  defaultValue?: string;
  onChange?: (value: string) => void;
}

/**
 * 56px dropdown on the field surface with a gold chevron — ISO, film
 * stock, aperture and shutter pickers.
 */
export function Select({ label, options, value, defaultValue, onChange }: SelectProps) {
  return (
    <label className="photon-select">
      {label && <FieldLabel>{label}</FieldLabel>}
      <span className="photon-select__box">
        <select
          className="photon-select__control"
          value={value}
          defaultValue={defaultValue}
          onChange={(e) => onChange?.(e.target.value)}
        >
          {options.map((opt) => {
            const o = typeof opt === 'string' ? { value: opt, label: opt } : opt;
            return (
              <option key={o.value} value={o.value}>
                {o.label}
              </option>
            );
          })}
        </select>
        <span className="photon-select__chevron" aria-hidden="true">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="6 9 12 15 18 9" />
          </svg>
        </span>
      </span>
    </label>
  );
}
