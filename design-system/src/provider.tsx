import * as React from 'react';

export interface PhotonProviderProps {
  /** Color theme. The app is dark-first; light mirrors the Android light palette. */
  theme?: 'dark' | 'light';
  /** Fill the viewport and paint the page background. Disable for embedded fragments. */
  fullscreen?: boolean;
  children?: React.ReactNode;
}

/**
 * Theme root for Photon Exposure Engine. Wrap every screen in it — the design
 * tokens, base font and page background live here. Without it components
 * render on a transparent background with inherited fonts.
 */
export function PhotonProvider({ theme = 'dark', fullscreen = true, children }: PhotonProviderProps) {
  return (
    <div
      className={'photon-root' + (fullscreen ? ' photon-root--fullscreen' : '')}
      data-theme={theme}
    >
      {children}
    </div>
  );
}
