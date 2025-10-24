/**
 * UI-related constants and configuration
 */

export const BREAKPOINTS = {
  XS: 320,
  SM: 768,
  MD: 1024,
  LG: 1440,
  XL: 1920,
} as const;

export const DEBOUNCE_DELAY = {
  SEARCH: 300,
  RESIZE: 100,
  SCROLL: 50,
} as const;

export const ANIMATION_DURATION = {
  SHORT: 200,
  MEDIUM: 300,
  LONG: 500,
} as const;

export const NOTIFICATION_DURATION = {
  SUCCESS: 3000,
  ERROR: 5000,
  WARNING: 4000,
  INFO: 3000,
} as const;

export const PRIORITY_COLORS = {
  HIGH: '#f44336', // Red
  MEDIUM: '#ff9800', // Orange
  LOW: '#4caf50', // Green
} as const;

export const STATUS_COLORS = {
  OPEN: '#2196f3', // Blue
  IN_PROGRESS: '#ff9800', // Orange
  HOLD: '#9c27b0', // Purple
  DONE: '#4caf50', // Green
  CLOSED: '#757575', // Grey
} as const;

export const CHART_COLORS = [
  '#8884d8',
  '#82ca9d',
  '#ffc658',
  '#ff7300',
  '#00ff00',
  '#ff00ff',
  '#00ffff',
  '#ff0000',
] as const;

export const KEYBOARD_SHORTCUTS = {
  NEW_TASK: 'ctrl+n',
  SAVE: 'ctrl+s',
  SEARCH: 'ctrl+k',
  REFRESH: 'f5',
  ESCAPE: 'escape',
} as const;

export const LOCAL_STORAGE_KEYS = {
  THEME_MODE: 'todo-app-theme-mode',
  PAGE_SIZE: 'todo-app-page-size',
  COLUMN_WIDTHS: 'todo-app-column-widths',
  FILTER_STATE: 'todo-app-filter-state',
} as const;

export const DEFAULT_PAGE_SIZE = 5;

export const TASK_FORM_VALIDATION = {
  TITLE: {
    MIN_LENGTH: 1,
    MAX_LENGTH: 255,
  },
  DESCRIPTION: {
    MAX_LENGTH: 1000,
  },
} as const;

export const EMPTY_STATES = {
  NO_TASKS: {
    title: 'No tasks found',
    description: 'Get started by creating your first task',
    actionLabel: 'Create Task',
  },
  NO_SEARCH_RESULTS: {
    title: 'No results found',
    description: 'Try adjusting your search criteria',
    actionLabel: 'Clear Search',
  },
  ERROR: {
    title: 'Something went wrong',
    description: 'Please try again or contact support if the problem persists',
    actionLabel: 'Retry',
  },
} as const;

export const LOADING_MESSAGES = {
  LOADING_TASKS: 'Loading tasks...',
  CREATING_TASK: 'Creating task...',
  UPDATING_TASK: 'Updating task...',
  DELETING_TASK: 'Deleting task...',
  LOADING_STATISTICS: 'Loading statistics...',
} as const;
