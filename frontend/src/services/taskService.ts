/**
 * Task service for API operations
 */

import { apiClient, apiRequest } from './api';
import { API_ENDPOINTS } from '@/constants/api';
import type {
  TaskResponseDTO,
  TaskCreateRequestDTO,
  TaskUpdateRequestDTO,
  TaskStatisticsResponseDTO,
  PageResponse,
  TaskFilters,
} from '@/types/api';

export class TaskService {
  /**
   * Get all tasks with pagination and filtering
   */
  static async getTasks(filters: TaskFilters = {}): Promise<PageResponse<TaskResponseDTO>> {
    const params = new URLSearchParams();
    
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size !== undefined) params.append('size', filters.size.toString());
    if (filters.sortBy) params.append('sortBy', filters.sortBy);
    if (filters.sortDirection) params.append('sortDirection', filters.sortDirection);
    if (filters.statusId !== undefined) params.append('statusId', filters.statusId.toString());
    if (filters.priorityId !== undefined) params.append('priorityId', filters.priorityId.toString());
    if (filters.searchTerm) params.append('searchTerm', filters.searchTerm);

    const endpoint = filters.statusId || filters.priorityId || filters.searchTerm 
      ? API_ENDPOINTS.TASKS_FILTER 
      : API_ENDPOINTS.TASKS;

    return apiRequest(() => 
      apiClient.get(`${endpoint}?${params.toString()}`)
    );
  }

  /**
   * Get task by ID
   */
  static async getTaskById(id: number): Promise<TaskResponseDTO> {
    return apiRequest(() => 
      apiClient.get(API_ENDPOINTS.TASK_BY_ID(id))
    );
  }

  /**
   * Create new task
   */
  static async createTask(task: TaskCreateRequestDTO): Promise<TaskResponseDTO> {
    return apiRequest(() => 
      apiClient.post(API_ENDPOINTS.TASKS, task)
    );
  }

  /**
   * Update existing task
   */
  static async updateTask(id: number, task: TaskUpdateRequestDTO): Promise<TaskResponseDTO> {
    return apiRequest(() => 
      apiClient.put(API_ENDPOINTS.TASK_BY_ID(id), task)
    );
  }

  /**
   * Delete task (soft delete)
   */
  static async deleteTask(id: number): Promise<void> {
    return apiRequest(() => 
      apiClient.delete(API_ENDPOINTS.TASK_BY_ID(id))
    );
  }

  /**
   * Delete multiple tasks
   */
  static async deleteTasks(ids: number[]): Promise<void> {
    await Promise.all(ids.map(id => this.deleteTask(id)));
  }

  /**
   * Update task status
   */
  static async updateTaskStatus(id: number, statusId: number): Promise<TaskResponseDTO> {
    return this.updateTask(id, { taskStatusId: statusId });
  }

  /**
   * Update multiple task statuses
   */
  static async updateTasksStatus(ids: number[], statusId: number): Promise<void> {
    await Promise.all(ids.map(id => this.updateTaskStatus(id, statusId)));
  }

  /**
   * Search tasks by title or description
   */
  static async searchTasks(
    searchTerm: string, 
    filters: Omit<TaskFilters, 'searchTerm'> = {}
  ): Promise<PageResponse<TaskResponseDTO>> {
    return this.getTasks({ ...filters, searchTerm });
  }

  /**
   * Get tasks by status
   */
  static async getTasksByStatus(
    statusId: number, 
    filters: Omit<TaskFilters, 'statusId'> = {}
  ): Promise<PageResponse<TaskResponseDTO>> {
    return this.getTasks({ ...filters, statusId });
  }

  /**
   * Get tasks by priority
   */
  static async getTasksByPriority(
    priorityId: number, 
    filters: Omit<TaskFilters, 'priorityId'> = {}
  ): Promise<PageResponse<TaskResponseDTO>> {
    return this.getTasks({ ...filters, priorityId });
  }

  /**
   * Get task statistics
   */
  static async getTaskStatistics(): Promise<TaskStatisticsResponseDTO> {
    return apiRequest(() => 
      apiClient.get(API_ENDPOINTS.TASKS_STATISTICS)
    );
  }
}
