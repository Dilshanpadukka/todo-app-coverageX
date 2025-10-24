/**
 * Dashboard page component
 */

import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  useTheme,
} from '@mui/material';
import { Add as AddIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import { StatsCards } from '@/components/dashboard/StatsCards';
import { TaskBoard } from '@/components/tasks/TaskBoard';
import { TaskForm } from '@/components/tasks/TaskForm';
import { DeleteConfirmDialog } from '@/components/common/ConfirmDialog';
import {
  useTaskStatistics,
  useTasks,
  useCreateTask,
  useUpdateTask,
  useDeleteTask,
} from '@/hooks/useTasks';
import { useUIStore } from '@/stores/uiStore';
import { useCommonShortcuts } from '@/hooks/useKeyboardShortcuts';
import { useNavigate } from 'react-router-dom';
import type {
  TaskResponseDTO,
  TaskCreateRequestDTO,
  TaskUpdateRequestDTO,
  TaskFilters,
  PageResponse,
  TaskStatisticsResponseDTO,
} from '@/types/api';

const BOARD_TASK_LIMIT = 100;

export const Dashboard: React.FC = () => {
  const theme = useTheme();
  const { modals, setModal, selectedTaskId, setSelectedTaskId } = useUIStore();
  const navigate = useNavigate();

  const boardFilters = React.useMemo<TaskFilters>(
    () => ({
      page: 0,
      size: BOARD_TASK_LIMIT,
      sortBy: 'createDate',
      sortDirection: 'DESC',
    }),
    []
  );

  const [selectedTask, setSelectedTask] =
    React.useState<TaskResponseDTO | null>(null);

  // API hooks
  const {
    data: statistics,
    isLoading: statsLoading,
    refetch: refetchStats,
  } = useTaskStatistics();
  const {
    data: tasks,
    isLoading: tasksLoading,
    error: tasksError,
    refetch: refetchTasks,
  } = useTasks(boardFilters);

  const typedStatistics = statistics as TaskStatisticsResponseDTO | undefined;
  const typedTasks = tasks as PageResponse<TaskResponseDTO> | undefined;
  const createTaskMutation = useCreateTask();
  const updateTaskMutation = useUpdateTask();
  const deleteTaskMutation = useDeleteTask();

  // Keyboard shortcuts
  useCommonShortcuts({
    onNew: () => handleCreateTask(),
    onRefresh: () => handleRefresh(),
  });

  const handleCreateTask = () => {
    setSelectedTask(null);
    setModal('createTask', true);
  };

  const handleEditTask = (task: TaskResponseDTO) => {
    setSelectedTask(task);
    setModal('editTask', true);
  };

  const handleDeleteTask = (taskId: number) => {
    setSelectedTaskId(taskId);
    setModal('confirmDelete', true);
  };

  const handleTaskStatusChange = (taskId: number, statusId: number) => {
    updateTaskMutation.mutate({ id: taskId, task: { taskStatusId: statusId } });
  };

  const handleFormSubmit = (
    data: TaskCreateRequestDTO | TaskUpdateRequestDTO
  ) => {
    if (selectedTask) {
      // Update existing task
      updateTaskMutation.mutate(
        { id: selectedTask.id, task: data as TaskUpdateRequestDTO },
        {
          onSuccess: () => {
            setModal('editTask', false);
            setSelectedTask(null);
          },
        }
      );
    } else {
      // Create new task
      createTaskMutation.mutate(data as TaskCreateRequestDTO, {
        onSuccess: () => {
          setModal('createTask', false);
        },
      });
    }
  };

  const handleConfirmDelete = () => {
    if (selectedTaskId) {
      deleteTaskMutation.mutate(selectedTaskId, {
        onSuccess: () => {
          setModal('confirmDelete', false);
          setSelectedTaskId(null);
        },
      });
    }
  };

  const handleRefresh = () => {
    refetchStats();
    refetchTasks();
  };

  const handleRetry = () => {
    refetchTasks();
  };

  const handleViewAll = () => {
    navigate('/tasks');
  };

  return (
    <Box>
      {/* Header */}
      <Box
        display="flex"
        alignItems="center"
        justifyContent="space-between"
        mb={4}
        flexWrap="wrap"
        gap={2}
      >
        <Box>
          <Typography
            variant="h4"
            component="h1"
            sx={{
              fontWeight: 700,
              color: theme.palette.text.primary,
              mb: 0.5,
            }}
          >
            Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage your tasks and track your progress
          </Typography>
        </Box>

        <Box display="flex" gap={1}>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={handleRefresh}
            sx={{ borderRadius: 2 }}
          >
            Refresh
          </Button>

          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleCreateTask}
            sx={{ borderRadius: 2 }}
          >
            New Task
          </Button>
        </Box>
      </Box>

      {/* Statistics Cards */}
      <Box mb={4}>
        <StatsCards statistics={typedStatistics} loading={statsLoading} />
      </Box>

      {/* Recent Tasks */}
      <Card>
        <CardContent>
          <Box
            display="flex"
            alignItems="center"
            justifyContent="space-between"
            mb={3}
          >
            <Typography variant="h6" sx={{ fontWeight: 600 }}>
              Tasks List
            </Typography>

            <Button
              variant="text"
              size="small"
              onClick={handleViewAll}
              sx={{ borderRadius: 1.5 }}
            >
              View All
            </Button>
          </Box>

          <TaskBoard
            tasks={typedTasks}
            loading={tasksLoading}
            error={tasksError}
            onTaskEdit={handleEditTask}
            onTaskDelete={handleDeleteTask}
            onTaskStatusChange={handleTaskStatusChange}
            onCreateTask={handleCreateTask}
            onRetry={handleRetry}
          />
        </CardContent>
      </Card>

      {/* Task Form Modal */}
      <TaskForm
        open={modals.createTask || modals.editTask}
        onClose={() => {
          setModal('createTask', false);
          setModal('editTask', false);
          setSelectedTask(null);
        }}
        onSubmit={handleFormSubmit}
        task={selectedTask}
        loading={createTaskMutation.isPending || updateTaskMutation.isPending}
      />

      {/* Delete Confirmation Dialog */}
      <DeleteConfirmDialog
        open={modals.confirmDelete}
        onConfirm={handleConfirmDelete}
        onCancel={() => {
          setModal('confirmDelete', false);
          setSelectedTaskId(null);
        }}
        itemName="task"
      />
    </Box>
  );
};

export default Dashboard;
