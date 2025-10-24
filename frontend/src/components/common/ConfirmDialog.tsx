import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  DialogContentText,
  Button,
  Box,
  useTheme,
} from '@mui/material';
import {
  Warning as WarningIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
} from '@mui/icons-material';
import type { ConfirmDialogProps } from '@/types/ui';

const getIcon = (severity: string) => {
  switch (severity) {
    case 'error':
      return <ErrorIcon color="error" />;
    case 'warning':
      return <WarningIcon color="warning" />;
    case 'info':
    default:
      return <InfoIcon color="info" />;
  }
};

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  open,
  title,
  message,
  onConfirm,
  onCancel,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  severity = 'warning',
}) => {
  const theme = useTheme();
  
  const handleConfirm = () => {
    onConfirm();
  };

  const handleCancel = () => {
    onCancel();
  };

  const getConfirmButtonColor = () => {
    switch (severity) {
      case 'error':
        return 'error';
      case 'warning':
        return 'warning';
      case 'info':
      default:
        return 'primary';
    }
  };

  return (
    <Dialog
      open={open}
      onClose={handleCancel}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 2,
        },
      }}
    >
      <DialogTitle
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1,
          pb: 1,
        }}
      >
        {getIcon(severity)}
        {title}
      </DialogTitle>
      
      <DialogContent>
        <DialogContentText
          sx={{
            color: theme.palette.text.primary,
            fontSize: '1rem',
            lineHeight: 1.5,
          }}
        >
          {message}
        </DialogContentText>
      </DialogContent>
      
      <DialogActions
        sx={{
          px: 3,
          pb: 2,
          gap: 1,
        }}
      >
        <Button
          onClick={handleCancel}
          variant="outlined"
          color="inherit"
          sx={{
            borderRadius: 1.5,
            px: 3,
          }}
        >
          {cancelText}
        </Button>
        <Button
          onClick={handleConfirm}
          variant="contained"
          color={getConfirmButtonColor()}
          sx={{
            borderRadius: 1.5,
            px: 3,
          }}
          autoFocus
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

/**
 * Predefined confirmation dialogs
 */
export const DeleteConfirmDialog: React.FC<{
  open: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  itemName?: string;
  itemCount?: number;
}> = ({ open, onConfirm, onCancel, itemName = 'item', itemCount = 1 }) => (
  <ConfirmDialog
    open={open}
    title={`Delete ${itemCount > 1 ? `${itemCount} items` : itemName}?`}
    message={
      itemCount > 1
        ? `Are you sure you want to delete these ${itemCount} items? This action cannot be undone.`
        : `Are you sure you want to delete this ${itemName}? This action cannot be undone.`
    }
    onConfirm={onConfirm}
    onCancel={onCancel}
    confirmText="Delete"
    cancelText="Cancel"
    severity="error"
  />
);

export const StatusChangeConfirmDialog: React.FC<{
  open: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  newStatus: string;
  itemCount?: number;
}> = ({ open, onConfirm, onCancel, newStatus, itemCount = 1 }) => (
  <ConfirmDialog
    open={open}
    title={`Change status to ${newStatus}?`}
    message={
      itemCount > 1
        ? `Are you sure you want to change the status of ${itemCount} tasks to "${newStatus}"?`
        : `Are you sure you want to change the task status to "${newStatus}"?`
    }
    onConfirm={onConfirm}
    onCancel={onCancel}
    confirmText="Change Status"
    cancelText="Cancel"
    severity="info"
  />
);

export const UnsavedChangesDialog: React.FC<{
  open: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}> = ({ open, onConfirm, onCancel }) => (
  <ConfirmDialog
    open={open}
    title="Unsaved Changes"
    message="You have unsaved changes. Are you sure you want to leave without saving?"
    onConfirm={onConfirm}
    onCancel={onCancel}
    confirmText="Leave"
    cancelText="Stay"
    severity="warning"
  />
);

export default ConfirmDialog;
