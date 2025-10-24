/**
 * UI-specific TypeScript interfaces and types
 */

export interface LoadingState {
  isLoading: boolean;
  error?: string | null;
}

export interface ConfirmDialogProps {
  open: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  confirmText?: string;
  cancelText?: string;
  severity?: 'warning' | 'error' | 'info';
}

export interface NotificationOptions {
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  duration?: number;
}

export interface TableColumn<T> {
  id: keyof T | string;
  label: string;
  sortable?: boolean;
  align?: 'left' | 'center' | 'right';
  width?: string | number;
  render?: (value: any, row: T) => React.ReactNode;
}

export interface PaginationConfig {
  page: number;
  size: number;
  total: number;
  pageSizeOptions: number[];
}

export interface FilterPanelState {
  open: boolean;
  statusFilters: number[];
  priorityFilter?: number;
  dateRange?: {
    start: Date | null;
    end: Date | null;
  };
}

export interface BulkActionConfig {
  selectedIds: number[];
  availableActions: BulkAction[];
}

export interface BulkAction {
  id: string;
  label: string;
  icon: React.ReactNode;
  action: (ids: number[]) => void;
  confirmRequired?: boolean;
  confirmMessage?: string;
}

export interface KeyboardShortcut {
  key: string;
  ctrlKey?: boolean;
  shiftKey?: boolean;
  altKey?: boolean;
  action: () => void;
  description: string;
}

export interface ThemeMode {
  mode: 'light' | 'dark' | 'system';
}

export interface ResponsiveBreakpoint {
  xs: boolean;
  sm: boolean;
  md: boolean;
  lg: boolean;
  xl: boolean;
}

export interface FormFieldError {
  field: string;
  message: string;
}

export interface ModalProps {
  open: boolean;
  onClose: () => void;
  title: string;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  fullWidth?: boolean;
}

export interface EmptyStateProps {
  title: string;
  description: string;
  icon?: React.ReactNode;
  action?: {
    label: string;
    onClick: () => void;
  };
}

export interface SkeletonConfig {
  rows: number;
  columns: number;
  height?: number;
}
