/**
 * Task list component with grid/list view and pagination
 */

import React from 'react';
import {
  Box,
  Grid,
  Pagination,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Typography,
  Paper,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import type { SelectChangeEvent } from '@mui/material/Select';
import { TaskCard } from './TaskCard';
import { LoadingSkeleton } from '@/components/common/LoadingSkeleton';
import {
  NoTasksEmptyState,
  NoSearchResultsEmptyState,
  ErrorEmptyState,
} from '@/components/common/EmptyState';
import { useUIStore } from '@/stores/uiStore';
import { PAGE_SIZE_OPTIONS } from '@/constants/api';
import type { TaskResponseDTO, PageResponse } from '@/types/api';

interface TaskListProps {
  tasks?: PageResponse<TaskResponseDTO>;
  loading?: boolean;
  error?: Error | null;
  onTaskEdit?: (task: TaskResponseDTO) => void;
  onTaskDelete?: (taskId: number) => void;
  onTaskStatusChange?: (taskId: number, statusId: number) => void;
  onTaskView?: (task: TaskResponseDTO) => void;
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
  onCreateTask?: () => void;
  onRetry?: () => void;
  searchTerm?: string;
  onClearSearch?: () => void;
}

export const TaskList: React.FC<TaskListProps> = ({
  tasks,
  loading = false,
  error,
  onTaskEdit,
  onTaskDelete,
  onTaskStatusChange,
  onTaskView,
  onPageChange,
  onPageSizeChange,
  onCreateTask,
  onRetry,
  searchTerm,
  onClearSearch,
}) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const { pageSize, setPageSize, bulkActions } = useUIStore();

  const handlePageChange = (_: React.ChangeEvent<unknown>, page: number) => {
    onPageChange?.(page - 1); // Convert to 0-based index
  };

  const handlePageSizeChange = (event: SelectChangeEvent<number>) => {
    const newSize = Number(event.target.value);
    setPageSize(newSize);
    onPageSizeChange?.(newSize);
  };

  // Loading state
  if (loading && !tasks) {
    return <LoadingSkeleton variant="list" config={{ rows: 5, columns: 1 }} />;
  }

  // Error state
  if (error) {
    return <ErrorEmptyState onRetry={onRetry} />;
  }

  // No tasks state
  if (!tasks || tasks.content.length === 0) {
    if (searchTerm) {
      return (
        <NoSearchResultsEmptyState
          searchTerm={searchTerm}
          onClearSearch={onClearSearch}
        />
      );
    }
    return <NoTasksEmptyState onCreateTask={onCreateTask} />;
  }

  return (
    <Box>
      {/* Results Summary */}
      <Box
        display="flex"
        alignItems="center"
        justifyContent="space-between"
        mb={3}
        flexWrap="wrap"
        gap={2}
      >
        <Typography variant="body2" color="text.secondary">
          Showing {tasks.content.length} of {tasks.totalElements} tasks
          {searchTerm && ` for "${searchTerm}"`}
        </Typography>

        {/* Page Size Selector */}
        <FormControl size="small" sx={{ minWidth: 120 }}>
          <InputLabel id="page-size-label">Per page</InputLabel>
          <Select
            value={pageSize}
            labelId="page-size-label"
            id="page-size-select"
            label="Per page"
            onChange={handlePageSizeChange}
            sx={{ borderRadius: 1.5 }}
          >
            {PAGE_SIZE_OPTIONS.map(size => (
              <MenuItem key={size} value={size}>
                {size}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {/* Task Grid */}
      <Grid container spacing={3}>
        {tasks.content.map(task => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={task.id}>
            <TaskCard
              task={task}
              onEdit={onTaskEdit}
              onDelete={onTaskDelete}
              onStatusChange={onTaskStatusChange}
              onView={onTaskView}
              selected={bulkActions.selectedIds.includes(task.id)}
              onSelect={taskId => {
                // Handle bulk selection if needed
                console.log('Selected task:', taskId);
              }}
            />
          </Grid>
        ))}
      </Grid>

      {/* Loading overlay for pagination */}
      {loading && tasks && (
        <Box
          position="absolute"
          top={0}
          left={0}
          right={0}
          bottom={0}
          bgcolor="rgba(255, 255, 255, 0.7)"
          display="flex"
          alignItems="center"
          justifyContent="center"
          zIndex={1}
        >
          <LoadingSkeleton variant="card" />
        </Box>
      )}

      {/* Pagination */}
      {tasks.totalPages > 1 && (
        <Paper
          elevation={0}
          sx={{
            mt: 4,
            p: 2,
            display: 'flex',
            justifyContent: 'center',
            backgroundColor: 'transparent',
          }}
        >
          <Pagination
            count={tasks.totalPages}
            page={tasks.number + 1} // Convert from 0-based to 1-based
            onChange={handlePageChange}
            color="primary"
            size={isMobile ? 'small' : 'medium'}
            showFirstButton
            showLastButton
            sx={{
              '& .MuiPaginationItem-root': {
                borderRadius: 1.5,
              },
            }}
          />
        </Paper>
      )}
    </Box>
  );
};

export default TaskList;
