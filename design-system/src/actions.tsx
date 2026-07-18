import * as React from 'react';

export interface ButtonProps {
  /** `primary` — solid gold; `outlined` — transparent with strong stroke. */
  variant?: 'primary' | 'outlined';
  /** Buttons span their container by default, like the app's 54px action buttons. */
  fullWidth?: boolean;
  disabled?: boolean;
  onClick?: () => void;
  children?: React.ReactNode;
}

/**
 * 54px action button with uppercase bold label ("CALCULATE EXPOSURE TIME").
 * Primary gold for the card's main action, outlined for secondary
 * navigation ("FORMULAS & THEORY").
 */
export function Button({ variant = 'primary', fullWidth = true, disabled, onClick, children }: ButtonProps) {
  const cls =
    'photon-button photon-button--' + variant + (fullWidth ? ' photon-button--full' : '');
  return (
    <button type="button" className={cls} disabled={disabled} onClick={onClick}>
      {children}
    </button>
  );
}

export interface IconButtonProps {
  /** Accessible name — required, the button renders only its icon. */
  label: string;
  /** `md` 48px (header actions) or `sm` 40px (inline actions like save/delete). */
  size?: 'md' | 'sm';
  /** Icon color: default text, gold for emphasis (save), danger orange for delete. */
  tone?: 'default' | 'gold' | 'danger';
  onClick?: () => void;
  /** An inline SVG sized ~20–22px, drawn with `currentColor`. */
  children?: React.ReactNode;
}

/**
 * Square icon button on a card-colored plate with 14px radius —
 * theme toggle, saved log, save result, delete entry.
 */
export function IconButton({ label, size = 'md', tone = 'default', onClick, children }: IconButtonProps) {
  const cls =
    'photon-icon-button' +
    (size === 'sm' ? ' photon-icon-button--sm' : '') +
    (tone !== 'default' ? ` photon-icon-button--${tone}` : '');
  return (
    <button type="button" className={cls} aria-label={label} title={label} onClick={onClick}>
      {children}
    </button>
  );
}

export interface SegmentedTabsItem {
  value: string;
  label: React.ReactNode;
  /** Optional inline SVG (~18px, `currentColor`) shown before the label. */
  icon?: React.ReactNode;
}

export interface SegmentedTabsProps {
  items: SegmentedTabsItem[];
  /** Controlled selected value. */
  value?: string;
  /** Initial value when uncontrolled. Defaults to the first item. */
  defaultValue?: string;
  onChange?: (value: string) => void;
}

/**
 * Full-width segmented control on an elevated track — the app's
 * Digital / Analog mode switch. The selected segment gets the solid
 * gold indicator with dark text.
 */
export function SegmentedTabs({ items, value, defaultValue, onChange }: SegmentedTabsProps) {
  const [inner, setInner] = React.useState(defaultValue ?? items[0]?.value);
  const selected = value ?? inner;
  return (
    <div className="photon-tabs" role="tablist">
      {items.map((item) => (
        <button
          key={item.value}
          type="button"
          role="tab"
          aria-selected={item.value === selected}
          className="photon-tabs__tab"
          onClick={() => {
            setInner(item.value);
            onChange?.(item.value);
          }}
        >
          {item.icon}
          {item.label}
        </button>
      ))}
    </div>
  );
}

export interface BadgeProps {
  /** `mode` — 40px gold square for mode letters and indices; `status` — small uppercase pill ("METERED"). */
  variant?: 'mode' | 'status';
  children?: React.ReactNode;
}

/** Gold-on-soft-gold badge: mode square (A / B / row index) or status pill. */
export function Badge({ variant = 'status', children }: BadgeProps) {
  return <span className={`photon-badge--${variant}`}>{children}</span>;
}
