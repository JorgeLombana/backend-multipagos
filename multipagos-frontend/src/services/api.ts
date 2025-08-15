import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import type { ApiResponse, ApiError } from '@/types';

/**
 * Centralized API service
 */
class ApiService {
  private readonly client: AxiosInstance;
  private readonly baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
  private readonly timeout = Number(import.meta.env.VITE_API_TIMEOUT) || 10000;

  constructor() {
    this.client = axios.create({
      baseURL: this.baseURL,
      timeout: this.timeout,
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
    });

    this.setupInterceptors();
  }

  /**
   * Setup request and response interceptors
   */
  private setupInterceptors(): void {
    this.client.interceptors.request.use(
      (config) => {
        const token = this.getStoredToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }

        if (import.meta.env.DEV) {
          console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`, {
            data: config.data,
            params: config.params,
          });
        }

        return config;
      },
      (error) => {
        console.error('Request interceptor error:', error);
        return Promise.reject(error);
      }
    );

    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        if (import.meta.env.DEV) {
          console.log(`API Response: ${response.config.method?.toUpperCase()} ${response.config.url}`, response.data);
        }
        return response;
      },
      (error) => {
        if (error.response?.status === 401) {
          this.handleUnauthorized();
        }

        if (import.meta.env.DEV) {
          console.error('API Error:', {
            url: error.config?.url,
            method: error.config?.method,
            status: error.response?.status,
            data: error.response?.data,
          });
        }

        return Promise.reject(error);
      }
    );
  }

  /**
   * Handle unauthorized access
   */
  private handleUnauthorized(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');

    const currentPath = window.location.pathname;
    if (currentPath !== '/login' && currentPath !== '/register') {
      window.dispatchEvent(
        new CustomEvent('auth:unauthorized', {
          detail: { redirectTo: '/login' },
        })
      );

      window.location.href = '/login';
    }
  }

  /**
   * Get token from localStorage
   */
  private getStoredToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  /**
   * Generic GET request
   */
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.get<ApiResponse<T>>(url, config);
      return this.validateResponse(response.data);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  /**
   * Generic POST request
   */
  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    try {
      const response = await this.client.post<ApiResponse<T>>(url, data, config);
      return this.validateResponse(response.data);
    } catch (error) {
      throw this.handleError(error);
    }
  }

  /**
   * Validate API response structure
   */
  private validateResponse<T>(response: ApiResponse<T>): ApiResponse<T> {
    if (!response.status || !response.message) {
      throw new Error('Invalid API response format');
    }
    return response;
  }

  /**
   * Enhanced error handling with detailed error information
   */
  private handleError(error: any): ApiError {
    if (!error.response) {
      return {
        error: 'Network Error',
        message: 'No se pudo conectar al servidor. Verifique su conexión a internet.',
        path: error.config?.url || '',
        timestamp: new Date().toISOString(),
        apiVersion: 'v1',
      };
    }

    const { status, data } = error.response;

    switch (status) {
      case 400:
        return {
          error: data.error || 'Bad Request',
          message: data.message || 'Solicitud inválida',
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
          validationErrors: data.validationErrors,
        };

      case 401:
        return {
          error: data.error || 'Unauthorized',
          message: data.message || 'Su sesión ha expirado. Por favor, inicie sesión nuevamente.',
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
        };

      case 403:
        return {
          error: data.error || 'Forbidden',
          message: data.message || 'No tiene permisos para realizar esta acción.',
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
        };

      case 404:
        return {
          error: data.error || 'Not Found',
          message: data.message || 'El recurso solicitado no fue encontrado.',
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
        };

      case 422:
        return {
          error: 'Validation Error',
          message: data.message || 'Los datos enviados no son válidos',
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
          validationErrors: data.validationErrors,
        };

      case 500:
        return {
          error: data.error || 'Internal Server Error',
          message: data.message || 'Error interno del servidor. Intente nuevamente más tarde.',
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
        };

      default:
        return {
          error: data.error || 'Unknown Error',
          message: data.message || `Error del servidor (${status})`,
          path: data.path || error.config?.url || '',
          timestamp: data.timestamp || new Date().toISOString(),
          apiVersion: data.apiVersion || 'v1',
          validationErrors: data.validationErrors,
        };
    }
  }

  /**
   * Get the base URL being used
   */
  getBaseUrl(): string {
    return this.baseURL;
  }

  /**
   * Check if API is accessible
   */
  async healthCheck(): Promise<boolean> {
    try {
      await this.get('/health');
      return true;
    } catch {
      return false;
    }
  }
}

export const apiService = new ApiService();
