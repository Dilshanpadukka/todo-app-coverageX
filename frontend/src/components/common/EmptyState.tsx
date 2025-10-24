import React from 'react';
import { Box, Typography, Button, Paper, useTheme } from '@mui/material';
import {
  TaskAlt as TaskIcon,
  Search as SearchIcon,
  Error as ErrorIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import type { EmptyStateProps } from '@/types/ui';

interface EmptyStateComponentProps extends EmptyStateProps {
  variant?: 'no-data' | 'no-results' | 'error';
  size?: 'small' | 'medium' | 'large';
}

const getDefaultIcon = (variant: string) => {
  switch (variant) {
    case 'no-results':
      return <SearchIcon />;
    case 'error':
      return <ErrorIcon />;
    case 'no-data':
    default:
      return <TaskIcon />;
  }
};

const getIconSize = (size: string) => {
  switch (size) {
    case 'small':
      return { fontSize: 48 };
    case 'large':
      return { fontSize: 96 };
    case 'medium':
    default:
      return { fontSize: 64 };
  }
};

export const EmptyState: React.FC<EmptyStateComponentProps> = ({
  title,
  description,
  icon,
  action,
  variant = 'no-data',
  size = 'medium',
}) => {
  const theme = useTheme();

  const defaultIcon = getDefaultIcon(variant);
  const renderedIcon = React.isValidElement(icon) ? icon : defaultIcon;
  const iconSize = getIconSize(size);

  const getColor = () => {
    switch (variant) {
      case 'error':
        return theme.palette.error.main;
      case 'no-results':
        return theme.palette.warning.main;
      case 'no-data':
      default:
        return theme.palette.text.secondary;
    }
  };

  return (
    <Paper
      elevation={0}
      sx={{
        p: 4,
        textAlign: 'center',
        backgroundColor: 'transparent',
        border: `1px dashed ${theme.palette.divider}`,
        borderRadius: 2,
      }}
    >
      <Box
        sx={{
          color: getColor(),
          mb: 2,
          display: 'flex',
          justifyContent: 'center',
        }}
      >
        {React.cloneElement(renderedIcon, {
          sx: iconSize,
        })}
      </Box>

      <Typography
        variant={size === 'large' ? 'h5' : size === 'small' ? 'body1' : 'h6'}
        component="h3"
        gutterBottom
        sx={{
          fontWeight: 600,
          color: theme.palette.text.primary,
          mb: 1,
        }}
      >
        {title}
      </Typography>

      <Typography
        variant={size === 'large' ? 'body1' : 'body2'}
        color="text.secondary"
        sx={{
          mb: action ? 3 : 0,
          maxWidth: 400,
          mx: 'auto',
          lineHeight: 1.5,
        }}
      >
        {description}
      </Typography>

      {action && (
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={action.onClick}
          size={size === 'large' ? 'large' : 'medium'}
          sx={{
            borderRadius: 2,
            px: 3,
            py: 1,
          }}
        >
          {action.label}
        </Button>
      )}
    </Paper>
  );
};

/**
 * Predefined empty state variants
 */
export const NoTasksEmptyState: React.FC<{
  onCreateTask?: () => void;
}> = ({ onCreateTask }) => (
  <EmptyState
    variant="no-data"
    title="No tasks yet"
    description="Get started by creating your first task to organize your work and boost productivity."
    icon={<TaskIcon />}
    action={
      onCreateTask
        ? {
            label: 'Create First Task',
            onClick: onCreateTask,
          }
        : undefined
    }
  />
);

export const NoSearchResultsEmptyState: React.FC<{
  searchTerm?: string;
  onClearSearch?: () => void;
}> = ({ searchTerm, onClearSearch }) => (
  <EmptyState
    variant="no-results"
    title="No results found"
    description={
      searchTerm
        ? `No tasks match "${searchTerm}". Try adjusting your search criteria.`
        : 'No tasks match your current filters. Try adjusting your criteria.'
    }
    icon={<SearchIcon />}
    action={
      onClearSearch
        ? {
            label: 'Clear Search',
            onClick: onClearSearch,
          }
        : undefined
    }
  />
);

export const ErrorEmptyState: React.FC<{
  onRetry?: () => void;
}> = ({ onRetry }) => (
  <EmptyState
    variant="error"
    title="Something went wrong"
    description="We encountered an error while loading your tasks. Please try again."
    icon={<ErrorIcon />}
    action={
      onRetry
        ? {
            label: 'Try Again',
            onClick: onRetry,
          }
        : undefined
    }
  />
);

export default EmptyState;
