/**
 * Mock data for tests
 */

import type {
  TaskResponseDTO,
  PriorityTypeResponseDTO,
  TaskStatusTypeResponseDTO,
  PageResponse,
  TaskStatisticsResponseDTO,
} from '@/types/api';

export const mockPriorityTypes: PriorityTypeResponseDTO[] = [
  { id: 1, type: 'HIGH' },
  { id: 2, type: 'MEDIUM' },
  { id: 3, type: 'LOW' },
];

export const mockTaskStatusTypes: TaskStatusTypeResponseDTO[] = [
  { id: 1, type: 'OPEN' },
  { id: 2, type: 'IN_PROGRESS' },
  { id: 3, type: 'HOLD' },
  { id: 4, type: 'DONE' },
  { id: 5, type: 'CLOSED' },
];

export const mockTask: TaskResponseDTO = {
  id: 1,
  taskTitle: 'Test Task',
  description: 'Test description',
  createDate: '2024-01-01T00:00:00Z',
  lastStatusChangeDate: '2024-01-01T00:00:00Z',
  priority: mockPriorityTypes[0]!,
  taskStatus: mockTaskStatusTypes[0]!,
};

export const mockTasks: TaskResponseDTO[] = [
  mockTask,
  {
    id: 2,
    taskTitle: 'Second Task',
    description: 'Second description',
    createDate: '2024-01-02T00:00:00Z',
    lastStatusChangeDate: '2024-01-02T00:00:00Z',
    priority: mockPriorityTypes[1]!,
    taskStatus: mockTaskStatusTypes[1]!,
  },
  {
    id: 3,
    taskTitle: 'Third Task',
    description: 'Third description',
    createDate: '2024-01-03T00:00:00Z',
    lastStatusChangeDate: '2024-01-03T00:00:00Z',
    priority: mockPriorityTypes[2]!,
    taskStatus: mockTaskStatusTypes[3]!,
  },
];

export const mockTasksPage: PageResponse<TaskResponseDTO> = {
  content: mockTasks,
  totalElements: 3,
  totalPages: 1,
  size: 10,
  number: 0,
  numberOfElements: 3,
  first: true,
  last: true,
  empty: false,
};

export const mockEmptyPage: PageResponse<TaskResponseDTO> = {
  content: [],
  totalElements: 0,
  totalPages: 0,
  size: 10,
  number: 0,
  numberOfElements: 0,
  first: true,
  last: true,
  empty: true,
};

export const mockStatistics: TaskStatisticsResponseDTO = {
  totalTasks: 10,
  tasksByStatus: {
    OPEN: 3,
    IN_PROGRESS: 2,
    HOLD: 1,
    DONE: 3,
    CLOSED: 1,
  },
  tasksByPriority: {
    HIGH: 2,
    MEDIUM: 5,
    LOW: 3,
  },
  completedTasks: 4,
  activeTasks: 6,
};
