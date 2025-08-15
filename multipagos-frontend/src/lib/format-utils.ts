import { DATE_FORMATS } from './business-constants';

/**
 * Utility functions for common operations
 */

// Date formatting utilities
export const formatTransactionDate = (dateString: string) => {
  const date = new Date(dateString);
  return {
    date: date.toLocaleDateString(DATE_FORMATS.DATE_LOCALE, DATE_FORMATS.DATE_OPTIONS),
    time: date.toLocaleTimeString(DATE_FORMATS.DATE_LOCALE, DATE_FORMATS.TIME_OPTIONS),
  };
};

// Copy to clipboard utility
export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    // Fallback for older browsers
    try {
      const textArea = document.createElement('textarea');
      textArea.value = text;
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();
      const successful = document.execCommand('copy');
      document.body.removeChild(textArea);
      return successful;
    } catch {
      return false;
    }
  }
};

// Generate pagination numbers
export const generatePaginationNumbers = (
  currentPage: number,
  totalPages: number,
  maxVisible: number = 5
): number[] => {
  const pages: number[] = [];

  if (totalPages <= maxVisible) {
    for (let i = 0; i < totalPages; i++) {
      pages.push(i);
    }
  } else {
    let start = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages - 1, start + maxVisible - 1);

    if (end === totalPages - 1) {
      start = Math.max(0, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
  }

  return pages;
};

// Error message extraction
export const extractErrorMessage = (error: unknown): string => {
  if (typeof error === 'string') return error;
  if (error && typeof error === 'object' && 'message' in error) {
    return String(error.message);
  }
  if (error && typeof error === 'object' && 'error' in error) {
    return String(error.error);
  }
  return 'Error desconocido';
};

// Safe number parsing
export const safeParseInt = (value: string, fallback: number = 0): number => {
  const parsed = parseInt(value, 10);
  return isNaN(parsed) ? fallback : parsed;
};

export const safeParseFloat = (value: string, fallback: number = 0): number => {
  const parsed = parseFloat(value);
  return isNaN(parsed) ? fallback : parsed;
};

// Array utilities
export const filterCompletedTransactions = <T extends { status: string }>(transactions: T[]): T[] => {
  return transactions.filter((t) => t.status.toUpperCase() === 'SUCCESS' || t.status.toUpperCase() === 'COMPLETED');
};

// Validation utilities
export const isValidAmount = (amount: number, min: number, max: number): boolean => {
  return amount >= min && amount <= max;
};

// String utilities
export const truncateString = (str: string, length: number): string => {
  return str.length > length ? `${str.slice(0, length)}...` : str;
};

export const capitalizeFirst = (str: string): string => {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};
