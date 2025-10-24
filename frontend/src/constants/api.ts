/**
 * API-related constants and configuration
 */

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export const API_ENDPOINTS = {
  // Tasks
  TASKS: '/tasks',
  TASK_BY_ID: (id: number) => `/tasks/${id}`,
  TASKS_BY_STATUS: (statusId: number) => `/tasks/status/${statusId}`,
  TASKS_BY_PRIORITY: (priorityId: number) => `/tasks/priority/${priorityId}`,
  TASKS_SEARCH: '/tasks/search',
  TASKS_FILTER: '/tasks/filter',
  TASKS_STATISTICS: '/tasks/statistics',

  // Reference data
  PRIORITY_TYPES: '/priority-types',
  TASK_STATUS_TYPES: '/task-status-types',
} as const;

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500,
} as const;

export const QUERY_KEYS = {
  TASKS: 'tasks',
  TASK: 'task',
  TASK_STATISTICS: 'taskStatistics',
  PRIORITY_TYPES: 'priorityTypes',
  TASK_STATUS_TYPES: 'taskStatusTypes',
} as const;

export const DEFAULT_PAGE_SIZE = 5;
export const PAGE_SIZE_OPTIONS = [5, 10, 25, 50] as const;

export const SORT_DIRECTIONS = {
  ASC: 'ASC',
  DESC: 'DESC',
} as const;

export const DEFAULT_SORT = {
  field: 'createDate',
  direction: SORT_DIRECTIONS.DESC,
} as const;

export const RETRY_CONFIG = {
  attempts: 3,
  delay: 1000,
  backoff: 2,
} as const;

export const CACHE_TIME = {
  SHORT: 5 * 60 * 1000, // 5 minutes
  MEDIUM: 15 * 60 * 1000, // 15 minutes
  LONG: 60 * 60 * 1000, // 1 hour
} as const;

export const STALE_TIME = {
  SHORT: 30 * 1000, // 30 seconds
  MEDIUM: 2 * 60 * 1000, // 2 minutes
  LONG: 5 * 60 * 1000, // 5 minutes
} as const;
