import { apiService } from './api';
import { STORAGE_KEYS } from '@/lib/constants';
import type { LoginRequest, RegisterRequest, AuthResponse, RegisterResponse, User } from '@/types/auth';

/**
 * Authentication service
 */
class AuthService {
  private readonly TOKEN_KEY = STORAGE_KEYS.AUTH_TOKEN;
  private readonly USER_KEY = STORAGE_KEYS.AUTH_USER;

  /**
   * Register a new user
   * @param data - User registration data
   * @returns Promise with registration response
   */
  async register(data: RegisterRequest): Promise<RegisterResponse> {
    try {
      const response = await apiService.post<string>('/auth/register', data);

      return {
        status: response.status,
        message: response.message,
        data: response.data || 'Usuario registrado exitosamente',
        timestamp: response.timestamp,
        apiVersion: response.apiVersion,
      };
    } catch (error) {
      // Re-throw the error to be handled by the calling component
      throw error;
    }
  }

  /**
   * Login user and store authentication data
   * @param data - User login credentials
   * @returns Promise with authentication response
   */
  async login(data: LoginRequest): Promise<AuthResponse> {
    try {
      const response = await apiService.post<{ token: string; type: string; user: User }>('/auth/login', data);

      if (response.status === 'success' && response.data) {
        // Store authentication data securely
        this.setToken(response.data.token);
        this.setUser(response.data.user);
      }

      return {
        status: response.status,
        message: response.message,
        data: {
          token: response.data?.token || '',
          type: response.data?.type || 'Bearer',
          user: response.data?.user || ({} as User),
        },
        timestamp: response.timestamp,
        apiVersion: response.apiVersion,
      };
    } catch (error) {
      // Clear any partial data on login failure
      this.clearAuthData();
      throw error;
    }
  }

  /**
   * Logout user and clear all stored data
   */
  logout(): void {
    this.clearAuthData();
  }

  /**
   * Get stored authentication token
   */
  getToken(): string | null {
    try {
      return localStorage.getItem(this.TOKEN_KEY);
    } catch (error) {
      console.warn('Error accessing token from localStorage:', error);
      return null;
    }
  }

  /**
   * Set authentication token in secure storage
   * @param token - JWT token
   */
  setToken(token: string): void {
    try {
      if (!token) {
        console.warn('Attempted to set empty token');
        return;
      }
      localStorage.setItem(this.TOKEN_KEY, token);
    } catch (error) {
      console.error('Error storing token in localStorage:', error);
    }
  }

  /**
   * Clear authentication token from storage
   */
  clearToken(): void {
    try {
      localStorage.removeItem(this.TOKEN_KEY);
    } catch (error) {
      console.warn('Error removing token from localStorage:', error);
    }
  }

  /**
   * Get stored user data
   */
  getUser(): User | null {
    try {
      const userData = localStorage.getItem(this.USER_KEY);
      return userData ? JSON.parse(userData) : null;
    } catch (error) {
      console.warn('Error parsing user data from localStorage:', error);
      return null;
    }
  }

  /**
   * Set user data in secure storage
   * @param user - User information
   */
  setUser(user: User): void {
    try {
      if (!user || !user.id) {
        console.warn('Attempted to set invalid user data');
        return;
      }
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    } catch (error) {
      console.error('Error storing user data in localStorage:', error);
    }
  }

  /**
   * Clear user data from storage
   */
  clearUser(): void {
    try {
      localStorage.removeItem(this.USER_KEY);
    } catch (error) {
      console.warn('Error removing user data from localStorage:', error);
    }
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    const token = this.getToken();
    const user = this.getUser();

    return Boolean(token && user && user.id);
  }

  /**
   * Get current authenticated user or null
   */
  getCurrentUser(): User | null {
    return this.isAuthenticated() ? this.getUser() : null;
  }

  /**
   * Clear all authentication data
   */
  private clearAuthData(): void {
    this.clearToken();
    this.clearUser();
  }

  /**
   * Validate token format (basic check)
   * @param token - Token to validate
   */
  private isValidTokenFormat(token: string): boolean {
    // Basic JWT format check (3 parts separated by dots)
    const parts = token.split('.');
    return parts.length === 3;
  }

  /**
   * Check if token might be expired (basic client-side check)
   * Note: Server-side validation is still required
   */
  isTokenLikelyExpired(): boolean {
    const token = this.getToken();
    if (!token || !this.isValidTokenFormat(token)) {
      return true;
    }

    try {
      // Decode JWT payload (without verification)
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Math.floor(Date.now() / 1000);

      return payload.exp && payload.exp < currentTime;
    } catch {
      return true;
    }
  }
}

export const authService = new AuthService();
