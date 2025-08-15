export interface ApiResponse<T = any> {
  status: string;
  message: string;
  data?: T;
  timestamp: string;
  apiVersion: string;
}

export interface ApiError {
  status?: string;
  error: string;
  message: string;
  path: string;
  timestamp: string;
  apiVersion: string;
  validationErrors?: ApiValidationError[];
}

export interface ApiValidationError {
  field: string;
  rejectedValue: any;
  message: string;
}

export interface PaginationParams {
  page?: number;
  size?: number;
  sortField?: string;
  sortDirection?: 'ASC' | 'DESC';
}
