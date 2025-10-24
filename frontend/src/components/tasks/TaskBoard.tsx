/**
 * Task board component that groups tasks into status columns
 */

import React from 'react';
import { Box, Grid, Typography, Paper, Chip, useTheme } from '@mui/material';
import { alpha } from '@mui/material/styles';
import { TaskCard } from './TaskCard';
import { LoadingSkeleton } from '@/components/common/LoadingSkeleton';
import {
  NoTasksEmptyState,
  NoSearchResultsEmptyState,
  ErrorEmptyState,
} from '@/components/common/EmptyState';
import { getStatusColor } from '@/utils/helpers';
import type { TaskResponseDTO, PageResponse, TaskStatus } from '@/types/api';

interface TaskBoardProps {
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

type BoardColumnKey = 'OPEN' | 'IN_PROGRESS' | 'DONE';

const BOARD_COLUMNS: Array<{
  key: BoardColumnKey;
  title: string;
  helper: string;
}> = [
  { key: 'OPEN', title: 'Open', helper: 'Ready to be picked up' },
  {
    key: 'IN_PROGRESS',
    title: 'In Progress',
    helper: 'Currently being worked on',
  },
  { key: 'DONE', title: 'Done', helper: 'Completed recently' },
];

const isBoardStatus = (status: TaskStatus): status is BoardColumnKey => {
  return status === 'OPEN' || status === 'IN_PROGRESS' || status === 'DONE';
};

export const TaskBoard: React.FC<TaskBoardProps> = ({
  tasks,
  loading = false,
  error,
  onTaskEdit,
  onTaskDelete,
  onTaskStatusChange,
  onTaskView,
  onCreateTask,
  onRetry,
  searchTerm,
  onClearSearch,
}) => {
  const theme = useTheme();

  // Initial loading state
  if (loading && !tasks) {
    return <LoadingSkeleton variant="list" config={{ rows: 3, columns: 1 }} />;
  }

  // Error state
  if (error) {
    return <ErrorEmptyState onRetry={onRetry} />;
  }

  // Empty state handling
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

  const groupedTasks = React.useMemo(() => {
    const groups: Record<BoardColumnKey, TaskResponseDTO[]> = {
      OPEN: [],
      IN_PROGRESS: [],
      DONE: [],
    };

    tasks.content.forEach(task => {
      const status = task.taskStatus.type;
      if (isBoardStatus(status)) {
        groups[status].push(task);
      }
    });

    return groups;
  }, [tasks.content]);

  return (
    <Box position="relative">
      <Grid container spacing={3} alignItems="flex-start">
        {BOARD_COLUMNS.map(({ key, title, helper }) => {
          const columnTasks = groupedTasks[key];
          const statusColor = getStatusColor(key);
          return (
            <Grid item xs={12} md={4} key={key}>
              <Paper
                elevation={0}
                sx={{
                  p: 2,
                  borderRadius: 2,
                  border: `1px solid ${theme.palette.divider}`,
                  backgroundColor: theme.palette.background.paper,
                  minHeight: 280,
                }}
              >
                <Box
                  display="flex"
                  alignItems="center"
                  justifyContent="space-between"
                  mb={2}
                >
                  <Box>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                      {title}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {helper}
                    </Typography>
                  </Box>
                  <Chip
                    label={columnTasks.length}
                    size="small"
                    sx={{
                      fontWeight: 600,
                      color: statusColor,
                      backgroundColor: alpha(statusColor, 0.12),
                    }}
                  />
                </Box>

                <Box display="flex" flexDirection="column" gap={2}>
                  {columnTasks.length > 0 ? (
                    columnTasks.map(task => (
                      <TaskCard
                        key={task.id}
                        task={task}
                        onEdit={onTaskEdit}
                        onDelete={onTaskDelete}
                        onStatusChange={onTaskStatusChange}
                        onView={onTaskView}
                      />
                    ))
                  ) : (
                    <Box
                      sx={{
                        p: 2,
                        borderRadius: 1,
                        backgroundColor: alpha(theme.palette.action.hover, 0.4),
                        textAlign: 'center',
                      }}
                    >
                      <Typography variant="body2" color="text.secondary">
                        No {title.toLowerCase()} tasks
                      </Typography>
                    </Box>
                  )}
                </Box>
              </Paper>
            </Grid>
          );
        })}
      </Grid>

      {loading && tasks && (
        <Box
          position="absolute"
          top={0}
          left={0}
          right={0}
          bottom={0}
          display="flex"
          alignItems="center"
          justifyContent="center"
          bgcolor="rgba(255, 255, 255, 0.7)"
          zIndex={1}
        >
          <LoadingSkeleton variant="list" config={{ rows: 3, columns: 1 }} />
        </Box>
      )}
    </Box>
  );
};

export default TaskBoard;
