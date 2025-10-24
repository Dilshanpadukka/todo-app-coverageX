/**
 * Reference data service for priority types and task status types
 */

import { apiClient, apiRequest } from './api';
import { API_ENDPOINTS } from '@/constants/api';
import type { PriorityTypeResponseDTO, TaskStatusTypeResponseDTO } from '@/types/api';

export class ReferenceDataService {
  /**
   * Get all priority types
   */
  static async getPriorityTypes(): Promise<PriorityTypeResponseDTO[]> {
    return apiRequest(() => 
      apiClient.get(API_ENDPOINTS.PRIORITY_TYPES)
    );
  }

  /**
   * Get priority type by ID
   */
  static async getPriorityTypeById(id: number): Promise<PriorityTypeResponseDTO> {
    return apiRequest(() => 
      apiClient.get(`${API_ENDPOINTS.PRIORITY_TYPES}/${id}`)
    );
  }

  /**
   * Get all task status types
   */
  static async getTaskStatusTypes(): Promise<TaskStatusTypeResponseDTO[]> {
    return apiRequest(() => 
      apiClient.get(API_ENDPOINTS.TASK_STATUS_TYPES)
    );
  }

  /**
   * Get task status type by ID
   */
  static async getTaskStatusTypeById(id: number): Promise<TaskStatusTypeResponseDTO> {
    return apiRequest(() => 
      apiClient.get(`${API_ENDPOINTS.TASK_STATUS_TYPES}/${id}`)
    );
  }
}
