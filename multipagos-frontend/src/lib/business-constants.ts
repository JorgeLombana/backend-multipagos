/**
 * Business constants and configuration values
 */

// Transaction limits and validation
export const TOPUP_LIMITS = {
  MIN_AMOUNT: 1000,
  MAX_AMOUNT: 100000,
  PHONE_LENGTH: 10,
  PHONE_PREFIX: '3',
} as const;

// Pagination settings
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [2, 5, 10, 20, 50],
  MAX_VISIBLE_PAGES: 5,
} as const;

// Quick amount options for topup
export const QUICK_AMOUNTS = [1000, 2000, 5000, 10000, 20000, 50000] as const;

// Timeout and retry configuration
export const API_CONFIG = {
  TIMEOUT: 10000,
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000,
} as const;

// Status mappings
export const TRANSACTION_STATUS = {
  SUCCESS: 'SUCCESS',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
  PENDING: 'PENDING',
} as const;

// Supplier information
export const SUPPLIERS = {
  CLARO: { id: '8753', name: 'Claro' },
  MOVISTAR: { id: '9773', name: 'Movistar' },
  TIGO: { id: '3398', name: 'Tigo' },
  WOM: { id: '4689', name: 'WOM' },
} as const;

// Form validation patterns
export const VALIDATION_PATTERNS = {
  PHONE: /^3\d{9}$/,
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
} as const;

// Time formats
export const DATE_FORMATS = {
  DATE_LOCALE: 'es-CO',
  DATE_OPTIONS: {
    year: 'numeric' as const,
    month: 'long' as const,
    day: 'numeric' as const,
  },
  TIME_OPTIONS: {
    hour: '2-digit' as const,
    minute: '2-digit' as const,
    second: '2-digit' as const,
  },
} as const;
