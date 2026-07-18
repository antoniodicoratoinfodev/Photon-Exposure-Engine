import * as React from 'react';

export interface DataTableProps {
  /** Header labels — rendered on the solid gold header row. */
  columns: React.ReactNode[];
  /** Row cells, monospace. */
  rows: React.ReactNode[][];
}

/**
 * Exposure-map table: gold header row with dark bold labels, monospace
 * body, alternating row tint, rounded 14px frame.
 */
export function DataTable({ columns, rows }: DataTableProps) {
  return (
    <div className="photon-table-wrap">
      <table className="photon-table">
        <thead>
          <tr>
            {columns.map((c, i) => (
              <th key={i}>{c}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={i}>
              {row.map((cell, j) => (
                <td key={j}>{cell}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export interface FormulaCodeProps {
  children?: React.ReactNode;
}

/**
 * Monospace formula block in light gold on the field surface with a
 * gold-tinted stroke — "EV = log₂(N² / t)". Preserves line breaks.
 */
export function FormulaCode({ children }: FormulaCodeProps) {
  return <pre className="photon-formula-code">{children}</pre>;
}
