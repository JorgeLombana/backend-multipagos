export interface User {
  id: number;
  name: string;
  email: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  user: User;
}

export interface AuthResponse {
  status: string;
  message: string;
  data: LoginResponse;
  timestamp: string;
  apiVersion: string;
}

export interface RegisterResponse {
  status: string;
  message: string;
  data: string;
  timestamp: string;
  apiVersion: string;
}

export interface AuthError {
  error: string;
  message: string;
  path: string;
  timestamp: string;
  apiVersion: string;
  validationErrors?: AuthValidationError[];
}

export interface AuthValidationError {
  field: string;
  rejectedValue: any;
  message: string;
}
