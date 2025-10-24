/**
 * Custom hooks for task-related operations using TanStack Query
 */

import {
  useQuery,
  useMutation,
  useQueryClient,
  keepPreviousData,
} from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { TaskService } from '@/services/taskService';
import { ReferenceDataService } from '@/services/referenceDataService';
import { QUERY_KEYS, CACHE_TIME, STALE_TIME } from '@/constants/api';
import type {
  TaskResponseDTO,
  TaskCreateRequestDTO,
  TaskUpdateRequestDTO,
  TaskFilters,
  PageResponse,
  TaskStatisticsResponseDTO,
  PriorityTypeResponseDTO,
  TaskStatusTypeResponseDTO,
} from '@/types/api';

/**
 * Hook for fetching tasks with pagination and filtering
 */
export const useTasks = (filters: TaskFilters = {}) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TASKS, filters],
    queryFn: () => TaskService.getTasks(filters),
    staleTime: STALE_TIME.SHORT,
    gcTime: CACHE_TIME.SHORT,
    placeholderData: keepPreviousData,
    refetchOnWindowFocus: false,
  });
};

/**
 * Hook for fetching a single task by ID
 */
export const useTask = (id: number | null) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TASK, id],
    queryFn: () => TaskService.getTaskById(id!),
    enabled: !!id,
    staleTime: STALE_TIME.MEDIUM,
    gcTime: CACHE_TIME.MEDIUM,
  });
};

/**
 * Hook for fetching task statistics
 */
export const useTaskStatistics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.TASK_STATISTICS],
    queryFn: () => TaskService.getTaskStatistics(),
    staleTime: STALE_TIME.SHORT,
    gcTime: CACHE_TIME.SHORT,
    refetchInterval: 30000, // Refetch every 30 seconds
  });
};

/**
 * Hook for fetching priority types
 */
export const usePriorityTypes = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.PRIORITY_TYPES],
    queryFn: () => ReferenceDataService.getPriorityTypes(),
    staleTime: STALE_TIME.LONG,
    gcTime: CACHE_TIME.LONG,
  });
};

/**
 * Hook for fetching task status types
 */
export const useTaskStatusTypes = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.TASK_STATUS_TYPES],
    queryFn: () => ReferenceDataService.getTaskStatusTypes(),
    staleTime: STALE_TIME.LONG,
    gcTime: CACHE_TIME.LONG,
  });
};

/**
 * Hook for creating a new task
 */
export const useCreateTask = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (task: TaskCreateRequestDTO) => TaskService.createTask(task),
    onSuccess: newTask => {
      // Invalidate and refetch tasks
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASK_STATISTICS] });

      // Add the new task to the cache
      queryClient.setQueryData([QUERY_KEYS.TASK, newTask.id], newTask);

      toast.success('Task created successfully');
    },
    onError: error => {
      console.error('Error creating task:', error);
      toast.error('Failed to create task');
    },
  });
};

/**
 * Hook for updating a task
 */
export const useUpdateTask = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, task }: { id: number; task: TaskUpdateRequestDTO }) =>
      TaskService.updateTask(id, task),
    onMutate: async ({ id, task }) => {
      // Cancel outgoing refetches
      await queryClient.cancelQueries({ queryKey: [QUERY_KEYS.TASK, id] });

      // Snapshot previous value
      const previousTask = queryClient.getQueryData<TaskResponseDTO>([
        QUERY_KEYS.TASK,
        id,
      ]);

      // Optimistically update
      if (previousTask) {
        queryClient.setQueryData([QUERY_KEYS.TASK, id], {
          ...previousTask,
          ...task,
        });
      }

      return { previousTask };
    },
    onSuccess: updatedTask => {
      // Update the specific task in cache
      queryClient.setQueryData([QUERY_KEYS.TASK, updatedTask.id], updatedTask);

      // Invalidate tasks list to reflect changes
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASK_STATISTICS] });

      toast.success('Task updated successfully');
    },
    onError: (error, { id }, context) => {
      // Rollback optimistic update
      if (context?.previousTask) {
        queryClient.setQueryData([QUERY_KEYS.TASK, id], context.previousTask);
      }

      console.error('Error updating task:', error);
      toast.error('Failed to update task');
    },
  });
};

/**
 * Hook for deleting a task
 */
export const useDeleteTask = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => TaskService.deleteTask(id),
    onSuccess: (_, deletedId) => {
      // Remove task from cache
      queryClient.removeQueries({ queryKey: [QUERY_KEYS.TASK, deletedId] });

      // Invalidate tasks list
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASK_STATISTICS] });

      toast.success('Task deleted successfully');
    },
    onError: error => {
      console.error('Error deleting task:', error);
      toast.error('Failed to delete task');
    },
  });
};

/**
 * Hook for bulk deleting tasks
 */
export const useDeleteTasks = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (ids: number[]) => TaskService.deleteTasks(ids),
    onSuccess: (_, deletedIds) => {
      // Remove tasks from cache
      deletedIds.forEach(id => {
        queryClient.removeQueries({ queryKey: [QUERY_KEYS.TASK, id] });
      });

      // Invalidate tasks list
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASK_STATISTICS] });

      toast.success(`${deletedIds.length} tasks deleted successfully`);
    },
    onError: error => {
      console.error('Error deleting tasks:', error);
      toast.error('Failed to delete tasks');
    },
  });
};

/**
 * Hook for updating task status
 */
export const useUpdateTaskStatus = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, statusId }: { id: number; statusId: number }) =>
      TaskService.updateTaskStatus(id, statusId),
    onMutate: async ({ id, statusId }) => {
      // Optimistic update for better UX
      await queryClient.cancelQueries({ queryKey: [QUERY_KEYS.TASK, id] });

      const previousTask = queryClient.getQueryData<TaskResponseDTO>([
        QUERY_KEYS.TASK,
        id,
      ]);

      if (previousTask) {
        queryClient.setQueryData([QUERY_KEYS.TASK, id], {
          ...previousTask,
          taskStatus: { ...previousTask.taskStatus, id: statusId },
          lastStatusChangeDate: new Date().toISOString(),
        });
      }

      return { previousTask };
    },
    onSuccess: updatedTask => {
      queryClient.setQueryData([QUERY_KEYS.TASK, updatedTask.id], updatedTask);
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASKS] });
      queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.TASK_STATISTICS] });

      toast.success('Task status updated');
    },
    onError: (error, { id }, context) => {
      if (context?.previousTask) {
        queryClient.setQueryData([QUERY_KEYS.TASK, id], context.previousTask);
      }

      console.error('Error updating task status:', error);
      toast.error('Failed to update task status');
    },
  });
};
