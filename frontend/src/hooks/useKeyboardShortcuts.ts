/**
 * Custom hook for keyboard shortcuts
 */

import { useEffect, useCallback } from 'react';
import type { KeyboardShortcut } from '@/types/ui';

/**
 * Hook for handling keyboard shortcuts
 * @param shortcuts - Array of keyboard shortcut configurations
 * @param enabled - Whether shortcuts are enabled (default: true)
 */
export const useKeyboardShortcuts = (
  shortcuts: KeyboardShortcut[],
  enabled = true
): void => {
  const handleKeyDown = useCallback(
    (event: KeyboardEvent) => {
      if (!enabled) return;

      // Don't trigger shortcuts when typing in input fields
      const target = event.target as HTMLElement;
      if (
        target.tagName === 'INPUT' ||
        target.tagName === 'TEXTAREA' ||
        target.contentEditable === 'true'
      ) {
        return;
      }

      const matchingShortcut = shortcuts.find(shortcut => {
        const keyMatches = shortcut.key.toLowerCase() === event.key.toLowerCase();
        const ctrlMatches = !!shortcut.ctrlKey === event.ctrlKey;
        const shiftMatches = !!shortcut.shiftKey === event.shiftKey;
        const altMatches = !!shortcut.altKey === event.altKey;

        return keyMatches && ctrlMatches && shiftMatches && altMatches;
      });

      if (matchingShortcut) {
        event.preventDefault();
        event.stopPropagation();
        matchingShortcut.action();
      }
    },
    [shortcuts, enabled]
  );

  useEffect(() => {
    if (!enabled) return;

    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [handleKeyDown, enabled]);
};

/**
 * Hook for a single keyboard shortcut
 * @param key - The key to listen for
 * @param action - The action to perform
 * @param options - Additional options (ctrl, shift, alt, enabled)
 */
export const useKeyboardShortcut = (
  key: string,
  action: () => void,
  options: {
    ctrlKey?: boolean;
    shiftKey?: boolean;
    altKey?: boolean;
    enabled?: boolean;
  } = {}
): void => {
  const { ctrlKey = false, shiftKey = false, altKey = false, enabled = true } = options;

  const shortcuts: KeyboardShortcut[] = [
    {
      key,
      ctrlKey,
      shiftKey,
      altKey,
      action,
      description: `${ctrlKey ? 'Ctrl+' : ''}${shiftKey ? 'Shift+' : ''}${altKey ? 'Alt+' : ''}${key}`,
    },
  ];

  useKeyboardShortcuts(shortcuts, enabled);
};

/**
 * Common keyboard shortcuts
 */
export const useCommonShortcuts = (actions: {
  onNew?: () => void;
  onSave?: () => void;
  onSearch?: () => void;
  onRefresh?: () => void;
  onEscape?: () => void;
}) => {
  const shortcuts: KeyboardShortcut[] = [];

  if (actions.onNew) {
    shortcuts.push({
      key: 'n',
      ctrlKey: true,
      action: actions.onNew,
      description: 'Create new task',
    });
  }

  if (actions.onSave) {
    shortcuts.push({
      key: 's',
      ctrlKey: true,
      action: actions.onSave,
      description: 'Save',
    });
  }

  if (actions.onSearch) {
    shortcuts.push({
      key: 'k',
      ctrlKey: true,
      action: actions.onSearch,
      description: 'Search',
    });
  }

  if (actions.onRefresh) {
    shortcuts.push({
      key: 'F5',
      action: actions.onRefresh,
      description: 'Refresh',
    });
  }

  if (actions.onEscape) {
    shortcuts.push({
      key: 'Escape',
      action: actions.onEscape,
      description: 'Close/Cancel',
    });
  }

  useKeyboardShortcuts(shortcuts);
};
