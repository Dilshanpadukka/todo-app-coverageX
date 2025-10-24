/**
 * Axios HTTP client configuration with interceptors and error handling
 */

import axios, { AxiosError, AxiosResponse } from 'axios';
import toast from 'react-hot-toast';
import { API_BASE_URL, HTTP_STATUS, RETRY_CONFIG } from '@/constants/api';
import type { ApiError } from '@/types/api';

// Create axios instance with default configuration
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth tokens, logging, etc.
apiClient.interceptors.request.use(
  config => {
    // Add timestamp to prevent caching
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now(),
      };
    }

    // Log request in development
    if (import.meta.env.DEV) {
      console.log(`🚀 ${config.method?.toUpperCase()} ${config.url}`, {
        params: config.params,
        data: config.data,
      });
    }

    return config;
  },
  error => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors and logging
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // Log successful responses in development
    if (import.meta.env.DEV) {
      console.log(
        `✅ ${response.config.method?.toUpperCase()} ${response.config.url}`,
        {
          status: response.status,
          data: response.data,
        }
      );
    }

    return response;
  },
  (error: AxiosError<ApiError>) => {
    // Log errors in development
    if (import.meta.env.DEV) {
      console.error(
        `❌ ${error.config?.method?.toUpperCase()} ${error.config?.url}`,
        {
          status: error.response?.status,
          data: error.response?.data,
        }
      );
    }

    // Handle different error types
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;

      switch (status) {
        case HTTP_STATUS.BAD_REQUEST:
          if (data.fieldErrors) {
            // Handle validation errors
            Object.entries(data.fieldErrors).forEach(([field, message]) => {
              toast.error(`${field}: ${message}`);
            });
          } else {
            toast.error(data.message || 'Invalid request');
          }
          break;

        case HTTP_STATUS.NOT_FOUND:
          toast.error(data.message || 'Resource not found');
          break;

        case HTTP_STATUS.INTERNAL_SERVER_ERROR:
          toast.error('Server error. Please try again later.');
          break;

        default:
          toast.error(data.message || 'An unexpected error occurred');
      }
    } else if (error.request) {
      // Network error
      toast.error('Network error. Please check your connection.');
    } else {
      // Other error
      toast.error('An unexpected error occurred');
    }

    return Promise.reject(error);
  }
);

/**
 * Retry function with exponential backoff
 */
export const retryRequest = async <T>(
  requestFn: () => Promise<T>,
  attempts: number = RETRY_CONFIG.attempts,
  delay: number = RETRY_CONFIG.delay
): Promise<T> => {
  try {
    return await requestFn();
  } catch (error) {
    if (attempts <= 1) {
      throw error;
    }

    await new Promise(resolve => setTimeout(resolve, delay));
    return retryRequest(requestFn, attempts - 1, delay * RETRY_CONFIG.backoff);
  }
};

/**
 * Generic API request wrapper with retry logic
 */
export const apiRequest = async <T>(
  requestFn: () => Promise<AxiosResponse<T>>,
  enableRetry = true
): Promise<T> => {
  const makeRequest = async () => {
    const response = await requestFn();
    return response.data;
  };

  if (enableRetry) {
    return retryRequest(makeRequest);
  }

  return makeRequest();
};

export default apiClient;
