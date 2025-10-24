/**
 * Tasks management page showing the full task list with pagination
 */

import React from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  useTheme,
} from '@mui/material';
import {
  Add as AddIcon,
  Refresh as RefreshIcon,
  Dashboard as DashboardIcon,
} from '@mui/icons-material';
import { TaskList } from '@/components/tasks/TaskList';
import { TaskForm } from '@/components/tasks/TaskForm';
import { DeleteConfirmDialog } from '@/components/common/ConfirmDialog';
import {
  useTasks,
  useCreateTask,
  useUpdateTask,
  useDeleteTask,
} from '@/hooks/useTasks';
import { useUIStore } from '@/stores/uiStore';
import { useNavigate } from 'react-router-dom';
import type {
  TaskResponseDTO,
  TaskCreateRequestDTO,
  TaskUpdateRequestDTO,
  TaskFilters,
  PageResponse,
} from '@/types/api';

export const Tasks: React.FC = () => {
  const theme = useTheme();
  const { modals, setModal, selectedTaskId, setSelectedTaskId, pageSize } =
    useUIStore();
  const navigate = useNavigate();

  const [filters, setFilters] = React.useState<TaskFilters>({
    page: 0,
    size: pageSize,
    sortBy: 'createDate',
    sortDirection: 'DESC',
  });

  const [selectedTask, setSelectedTask] =
    React.useState<TaskResponseDTO | null>(null);

  const {
    data: tasks,
    isLoading: tasksLoading,
    error: tasksError,
    refetch: refetchTasks,
  } = useTasks(filters);

  const typedTasks = tasks as PageResponse<TaskResponseDTO> | undefined;
  const createTaskMutation = useCreateTask();
  const updateTaskMutation = useUpdateTask();
  const deleteTaskMutation = useDeleteTask();

  React.useEffect(() => {
    setFilters(prev => ({ ...prev, size: pageSize }));
  }, [pageSize]);

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

  const handlePageChange = (page: number) => {
    setFilters(prev => ({ ...prev, page }));
  };

  const handlePageSizeChange = (size: number) => {
    setFilters(prev => ({ ...prev, size, page: 0 }));
  };

  const handleRefresh = () => {
    refetchTasks();
  };

  const handleRetry = () => {
    refetchTasks();
  };

  return (
    <Box>
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
            Tasks
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Browse and manage all tasks
          </Typography>
        </Box>

        <Box display="flex" gap={1}>
          <Button
            variant="outlined"
            startIcon={<DashboardIcon />}
            onClick={() => navigate('/dashboard')}
            sx={{ borderRadius: 2 }}
          >
            Board View
          </Button>

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

      <Card>
        <CardContent>
          <TaskList
            tasks={typedTasks}
            loading={tasksLoading}
            error={tasksError}
            onTaskEdit={handleEditTask}
            onTaskDelete={handleDeleteTask}
            onTaskStatusChange={handleTaskStatusChange}
            onPageChange={handlePageChange}
            onPageSizeChange={handlePageSizeChange}
            onCreateTask={handleCreateTask}
            onRetry={handleRetry}
          />
        </CardContent>
      </Card>

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

export default Tasks;
