/**
 * Enhanced type definitions for better type safety
 */

// Better error type instead of any
export interface AppError extends Error {
  code?: string;
  statusCode?: number;
  details?: Record<string, unknown>;
}

// Paginated response type
export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
  isFirst: boolean;
  isLast: boolean;
}

// Transaction status type
export type TransactionStatus = 'SUCCESS' | 'COMPLETED' | 'FAILED' | 'PENDING';

// Enhanced transaction interface
export interface EnhancedTransaction {
  id: string;
  cellPhone: string;
  value: number;
  supplierName: string;
  supplierId: string;
  status: TransactionStatus;
  transactionalID: string | null;
  createdAt: string;
  updatedAt: string;
  message: string;
}

// Form state type
export type FormState = 'idle' | 'loading' | 'success' | 'error';

// Supplier with additional metadata
export interface EnhancedSupplier {
  id: string;
  name: string;
  color: string;
  isAvailable: boolean;
  displayOrder: number;
}

// Component prop types for reusability
export interface BaseCardProps {
  title: string;
  description?: string;
  gradient?: string;
  icon?: React.ComponentType<{ className?: string }>;
  className?: string;
  children?: React.ReactNode;
}

export interface StatusBadgeProps {
  status: TransactionStatus;
  variant?: 'default' | 'outline' | 'secondary';
}

// Navigation item type
export interface NavigationItem {
  id: string;
  name: string;
  description: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
  isAvailable?: boolean;
}
