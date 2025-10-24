/**
 * Zod validation schemas for form validation
 */

import { z } from 'zod';
import { TASK_FORM_VALIDATION } from '@/constants/ui';

export const taskCreateSchema = z.object({
  taskTitle: z
    .string()
    .min(TASK_FORM_VALIDATION.TITLE.MIN_LENGTH, 'Title is required')
    .max(TASK_FORM_VALIDATION.TITLE.MAX_LENGTH, `Title must be less than ${TASK_FORM_VALIDATION.TITLE.MAX_LENGTH} characters`)
    .trim(),
  description: z
    .string()
    .max(TASK_FORM_VALIDATION.DESCRIPTION.MAX_LENGTH, `Description must be less than ${TASK_FORM_VALIDATION.DESCRIPTION.MAX_LENGTH} characters`)
    .optional()
    .or(z.literal('')),
  priorityId: z
    .number()
    .min(1, 'Priority is required'),
  taskStatusId: z
    .number()
    .min(1, 'Status is required'),
});

export const taskUpdateSchema = z.object({
  taskTitle: z
    .string()
    .min(TASK_FORM_VALIDATION.TITLE.MIN_LENGTH, 'Title is required')
    .max(TASK_FORM_VALIDATION.TITLE.MAX_LENGTH, `Title must be less than ${TASK_FORM_VALIDATION.TITLE.MAX_LENGTH} characters`)
    .trim()
    .optional(),
  description: z
    .string()
    .max(TASK_FORM_VALIDATION.DESCRIPTION.MAX_LENGTH, `Description must be less than ${TASK_FORM_VALIDATION.DESCRIPTION.MAX_LENGTH} characters`)
    .optional()
    .or(z.literal('')),
  priorityId: z
    .number()
    .min(1, 'Priority is required')
    .optional(),
  taskStatusId: z
    .number()
    .min(1, 'Status is required')
    .optional(),
});

export const searchSchema = z.object({
  searchTerm: z
    .string()
    .min(1, 'Search term is required')
    .max(100, 'Search term must be less than 100 characters')
    .trim(),
});

export const filterSchema = z.object({
  statusIds: z.array(z.number()).optional(),
  priorityId: z.number().optional(),
  dateRange: z.object({
    start: z.date().optional(),
    end: z.date().optional(),
  }).optional(),
});

export type TaskCreateFormData = z.infer<typeof taskCreateSchema>;
export type TaskUpdateFormData = z.infer<typeof taskUpdateSchema>;
export type SearchFormData = z.infer<typeof searchSchema>;
export type FilterFormData = z.infer<typeof filterSchema>;

/**
 * Validation helper functions
 */
export const validateTaskTitle = (title: string): string | null => {
  if (!title.trim()) {
    return 'Title is required';
  }
  if (title.length > TASK_FORM_VALIDATION.TITLE.MAX_LENGTH) {
    return `Title must be less than ${TASK_FORM_VALIDATION.TITLE.MAX_LENGTH} characters`;
  }
  return null;
};

export const validateTaskDescription = (description: string): string | null => {
  if (description.length > TASK_FORM_VALIDATION.DESCRIPTION.MAX_LENGTH) {
    return `Description must be less than ${TASK_FORM_VALIDATION.DESCRIPTION.MAX_LENGTH} characters`;
  }
  return null;
};

export const validateRequired = (value: any, fieldName: string): string | null => {
  if (value === null || value === undefined || value === '') {
    return `${fieldName} is required`;
  }
  return null;
};

export const validateEmail = (email: string): string | null => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return 'Please enter a valid email address';
  }
  return null;
};

export const validateUrl = (url: string): string | null => {
  try {
    new URL(url);
    return null;
  } catch {
    return 'Please enter a valid URL';
  }
};

export const validateDateRange = (start: Date | null, end: Date | null): string | null => {
  if (start && end && start > end) {
    return 'Start date must be before end date';
  }
  return null;
};
