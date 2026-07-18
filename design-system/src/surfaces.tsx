import * as React from 'react';
import { Badge } from './actions';

export interface CardProps {
  /**
   * `default` — standard graphite card with subtle stroke.
   * `accent` — gold-tinted stroke, used for the film profile card.
   * `result` — warm background + gold stroke + larger radius, reserved for the results panel.
   */
  variant?: 'default' | 'accent' | 'result';
  style?: React.CSSProperties;
  children?: React.ReactNode;
}

/**
 * Primary surface. Rounded 22px, 1px stroke, 20px padding, flat (no shadow).
 * Screens are vertical stacks of Cards with 12px gaps.
 */
export function Card({ variant = 'default', style, children }: CardProps) {
  const cls =
    'photon-card' +
    (variant === 'accent' ? ' photon-card--accent' : '') +
    (variant === 'result' ? ' photon-card--result' : '');
  return (
    <div className={cls} style={style}>
      {children}
    </div>
  );
}

export interface ResultGlowProps {
  children?: React.ReactNode;
}

/**
 * Amber gradient highlight panel that holds the headline measurements
 * inside the results card (EV readouts, saved triads).
 */
export function ResultGlow({ children }: ResultGlowProps) {
  return <div className="photon-result-glow">{children}</div>;
}

export interface PageHeaderProps {
  /** Gold uppercase eyebrow between the leading/trailing slots ("Light · Exposure · Reciprocity"). */
  eyebrow?: React.ReactNode;
  title: React.ReactNode;
  subtitle?: React.ReactNode;
  /** Left slot of the top row — typically an IconButton (theme toggle, back arrow). */
  leading?: React.ReactNode;
  /** Right slot of the top row — typically an IconButton (saved log). */
  trailing?: React.ReactNode;
}

/**
 * Screen header: icon-button row with a gold eyebrow, then a 29px medium
 * title and a secondary subtitle.
 */
export function PageHeader({ eyebrow, title, subtitle, leading, trailing }: PageHeaderProps) {
  return (
    <header className="photon-page-header">
      {(leading || eyebrow || trailing) && (
        <div className="photon-page-header__row">
          {leading}
          <div className="photon-page-header__eyebrow">
            {eyebrow && <div className="photon-section-label">{eyebrow}</div>}
          </div>
          {trailing}
        </div>
      )}
      <h1 className="photon-page-header__title">{title}</h1>
      {subtitle && <p className="photon-page-header__subtitle">{subtitle}</p>}
    </header>
  );
}

export interface ListItemProps {
  /** Short badge text shown in the leading gold square (an index number, "A", "B"). */
  badge?: React.ReactNode;
  /** Monospace bold headline — the exposure triad ("f/5.6 · 1/125 s · ISO 400"). */
  title: React.ReactNode;
  /** Monospace secondary line ("≈ standard 1/125"). */
  subtitle?: React.ReactNode;
  /** Tertiary metadata line (date, film stock). */
  meta?: React.ReactNode;
  /** Italic notes, clamped to two lines. */
  notes?: React.ReactNode;
  /** Right slot — typically a small danger IconButton for delete. */
  trailing?: React.ReactNode;
  onClick?: () => void;
}

/**
 * Saved-exposure row: leading mode badge, monospace triad, metadata,
 * optional trailing action. Stack with 10–12px gaps.
 */
export function ListItem({ badge, title, subtitle, meta, notes, trailing, onClick }: ListItemProps) {
  return (
    <div
      className={'photon-list-item' + (onClick ? ' photon-list-item--clickable' : '')}
      onClick={onClick}
      role={onClick ? 'button' : undefined}
    >
      {badge != null && (
        <span className="photon-list-item__badge">
          <Badge variant="mode">{badge}</Badge>
        </span>
      )}
      <div className="photon-list-item__body">
        <div className="photon-list-item__title">{title}</div>
        {subtitle && <div className="photon-list-item__subtitle">{subtitle}</div>}
        {meta && <div className="photon-list-item__meta">{meta}</div>}
        {notes && <div className="photon-list-item__notes">{notes}</div>}
      </div>
      {trailing}
    </div>
  );
}
