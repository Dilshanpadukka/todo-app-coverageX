/**
 * TypeScript interfaces matching backend DTOs
 * These types ensure type safety when communicating with the Spring Boot API
 */

export interface PriorityTypeResponseDTO {
  id: number;
  type: 'HIGH' | 'MEDIUM' | 'LOW';
}

export interface TaskStatusTypeResponseDTO {
  id: number;
  type: 'OPEN' | 'IN_PROGRESS' | 'HOLD' | 'DONE' | 'CLOSED';
}

export interface TaskResponseDTO {
  id: number;
  taskTitle: string;
  description?: string;
  createDate: string;
  lastStatusChangeDate: string;
  priority: PriorityTypeResponseDTO;
  taskStatus: TaskStatusTypeResponseDTO;
}

export interface TaskCreateRequestDTO {
  taskTitle: string;
  description?: string;
  priorityId: number;
  taskStatusId: number;
}

export interface TaskUpdateRequestDTO {
  taskTitle?: string;
  description?: string;
  priorityId?: number;
  taskStatusId?: number;
}

export interface TaskStatisticsResponseDTO {
  totalTasks: number;
  tasksByStatus: Record<string, number>;
  tasksByPriority: Record<string, number>;
  completedTasks: number;
  activeTasks: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ApiError {
  status: number;
  error: string;
  message: string;
  timestamp: string;
  path: string;
  fieldErrors?: Record<string, string>;
}

export interface TaskFilters {
  statusId?: number;
  priorityId?: number;
  searchTerm?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface SortConfig {
  field: keyof TaskResponseDTO | 'priority.type' | 'taskStatus.type';
  direction: 'ASC' | 'DESC';
}

export type TaskPriority = 'HIGH' | 'MEDIUM' | 'LOW';
export type TaskStatus = 'OPEN' | 'IN_PROGRESS' | 'HOLD' | 'DONE' | 'CLOSED';

export interface ChartData {
  name: string;
  value: number;
  color?: string;
}
