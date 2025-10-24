/**
 * Task form component for creating and editing tasks
 */

import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  Typography,
  IconButton,
  useTheme,
} from '@mui/material';
import {
  Close as CloseIcon,
  Save as SaveIcon,
  Add as AddIcon,
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { taskCreateSchema, taskUpdateSchema } from '@/utils/validation';
import { usePriorityTypes, useTaskStatusTypes } from '@/hooks/useTasks';
import { LoadingSkeleton } from '@/components/common/LoadingSkeleton';
import type {
  TaskResponseDTO,
  TaskCreateRequestDTO,
  TaskUpdateRequestDTO,
} from '@/types/api';
import type {
  TaskCreateFormData,
  TaskUpdateFormData,
} from '@/utils/validation';

interface TaskFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: TaskCreateRequestDTO | TaskUpdateRequestDTO) => void;
  task?: TaskResponseDTO | null;
  loading?: boolean;
}

export const TaskForm: React.FC<TaskFormProps> = ({
  open,
  onClose,
  onSubmit,
  task,
  loading = false,
}) => {
  const theme = useTheme();
  const isEditing = !!task;

  const { data: priorityTypes, isLoading: priorityLoading } =
    usePriorityTypes();
  const { data: statusTypes, isLoading: statusLoading } = useTaskStatusTypes();

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors, isDirty },
  } = useForm<TaskCreateFormData | TaskUpdateFormData>({
    resolver: zodResolver(isEditing ? taskUpdateSchema : taskCreateSchema),
    defaultValues: {
      taskTitle: task?.taskTitle || '',
      description: task?.description || '',
      priorityId: task?.priority.id || undefined,
      taskStatusId: task?.taskStatus.id || undefined,
    },
  });

  // Reset form when task changes
  React.useEffect(() => {
    if (open) {
      reset({
        taskTitle: task?.taskTitle || '',
        description: task?.description || '',
        priorityId: task?.priority.id || undefined,
        taskStatusId: task?.taskStatus.id || undefined,
      });
    }
  }, [task, open, reset]);

  const handleFormSubmit = (data: TaskCreateFormData | TaskUpdateFormData) => {
    onSubmit(data as TaskCreateRequestDTO | TaskUpdateRequestDTO);
  };

  const handleClose = () => {
    if (
      !isDirty ||
      window.confirm(
        'You have unsaved changes. Are you sure you want to close?'
      )
    ) {
      onClose();
    }
  };

  if (priorityLoading || statusLoading) {
    return (
      <Dialog open={open} maxWidth="sm" fullWidth>
        <DialogContent>
          <LoadingSkeleton variant="card" />
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
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
          justifyContent: 'space-between',
          pb: 1,
        }}
      >
        <Box display="flex" alignItems="center" gap={1}>
          {isEditing ? <SaveIcon /> : <AddIcon />}
          <Typography variant="h6" component="h2">
            {isEditing ? 'Edit Task' : 'Create New Task'}
          </Typography>
        </Box>

        <IconButton
          onClick={handleClose}
          size="small"
          sx={{ color: theme.palette.text.secondary }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <form onSubmit={handleSubmit(handleFormSubmit)}>
        <DialogContent sx={{ pt: 2 }}>
          <Box display="flex" flexDirection="column" gap={3}>
            {/* Task Title */}
            <Controller
              name="taskTitle"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Task Title"
                  fullWidth
                  required
                  error={!!errors.taskTitle}
                  helperText={errors.taskTitle?.message}
                  autoFocus
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      borderRadius: 2,
                    },
                  }}
                />
              )}
            />

            {/* Description */}
            <Controller
              name="description"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Description"
                  fullWidth
                  multiline
                  rows={3}
                  error={!!errors.description}
                  helperText={errors.description?.message}
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      borderRadius: 2,
                    },
                  }}
                />
              )}
            />

            {/* Priority and Status */}
            <Box display="flex" gap={2}>
              {/* Priority */}
              <Controller
                name="priorityId"
                control={control}
                render={({ field }) => (
                  <FormControl fullWidth required error={!!errors.priorityId}>
                    <InputLabel id="priority-label">Priority</InputLabel>
                    <Select
                      {...field}
                      labelId="priority-label"
                      id="priority-select"
                      label="Priority"
                      sx={{
                        borderRadius: 2,
                      }}
                    >
                      {priorityTypes?.map(priority => (
                        <MenuItem key={priority.id} value={priority.id}>
                          {priority.type}
                        </MenuItem>
                      ))}
                    </Select>
                    {errors.priorityId && (
                      <Typography
                        variant="caption"
                        color="error"
                        sx={{ mt: 0.5, ml: 1.5 }}
                      >
                        {errors.priorityId.message}
                      </Typography>
                    )}
                  </FormControl>
                )}
              />

              {/* Status */}
              <Controller
                name="taskStatusId"
                control={control}
                render={({ field }) => (
                  <FormControl fullWidth required error={!!errors.taskStatusId}>
                    <InputLabel id="status-label">Status</InputLabel>
                    <Select
                      {...field}
                      labelId="status-label"
                      id="status-select"
                      label="Status"
                      sx={{
                        borderRadius: 2,
                      }}
                    >
                      {statusTypes?.map(status => (
                        <MenuItem key={status.id} value={status.id}>
                          {status.type.replace('_', ' ')}
                        </MenuItem>
                      ))}
                    </Select>
                    {errors.taskStatusId && (
                      <Typography
                        variant="caption"
                        color="error"
                        sx={{ mt: 0.5, ml: 1.5 }}
                      >
                        {errors.taskStatusId.message}
                      </Typography>
                    )}
                  </FormControl>
                )}
              />
            </Box>
          </Box>
        </DialogContent>

        <DialogActions sx={{ px: 3, pb: 2, gap: 1 }}>
          <Button
            onClick={handleClose}
            variant="outlined"
            color="inherit"
            sx={{ borderRadius: 1.5, px: 3 }}
          >
            Cancel
          </Button>

          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            sx={{ borderRadius: 1.5, px: 3 }}
            startIcon={isEditing ? <SaveIcon /> : <AddIcon />}
          >
            {loading ? 'Saving...' : isEditing ? 'Update Task' : 'Create Task'}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

export default TaskForm;
