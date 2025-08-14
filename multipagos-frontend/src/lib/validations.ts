import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';
import { BUSINESS_RULES, PATTERNS } from './constants';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * Format currency for display
 * @param amount - Amount in basic unit (COP)
 * @returns Formatted currency string
 */
export function formatCurrency(amount: number): string {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount);
}

/**
 * Format phone number for display
 * @param phoneNumber - Raw phone number string
 * @returns Formatted phone number (XXX XXX XXXX)
 */
export function formatPhoneNumber(phoneNumber: string): string {
  if (!phoneNumber || phoneNumber.length !== 10) {
    return phoneNumber;
  }

  return `${phoneNumber.slice(0, 3)} ${phoneNumber.slice(3, 6)} ${phoneNumber.slice(6)}`;
}

/**
 * Format date for display
 * @param dateString - ISO date string
 * @returns Formatted date string
 */
export function formatDate(dateString: string): string {
  const date = new Date(dateString);

  return new Intl.DateTimeFormat('es-CO', {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}

/**
 * Format relative time (time ago)
 * @param dateString - ISO date string
 * @returns Relative time string
 */
export function formatRelativeTime(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffInSeconds < 60) return 'Hace un momento';

  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) return `Hace ${diffInMinutes} minuto${diffInMinutes > 1 ? 's' : ''}`;

  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) return `Hace ${diffInHours} hora${diffInHours > 1 ? 's' : ''}`;

  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 30) return `Hace ${diffInDays} día${diffInDays > 1 ? 's' : ''}`;

  return formatDate(dateString);
}

/**
 * Validate email format
 * @param email - Email string to validate
 * @returns True if valid email format
 */
export function isValidEmail(email: string): boolean {
  return PATTERNS.EMAIL.test(email);
}

/**
 * Validates a phone number according to business rules
 * @param phone - The phone number to validate
 * @returns boolean indicating if the phone number is valid
 */
export function isValidPhone(phone: string): boolean {
  return PATTERNS.PHONE.test(phone);
}

/**
 * Validate password strength
 * @param password - Password string to validate
 * @returns Object with validation result and message
 */
export function validatePassword(password: string): { isValid: boolean; message: string } {
  if (password.length < BUSINESS_RULES.PASSWORD.MIN_LENGTH) {
    return {
      isValid: false,
      message: `La contraseña debe tener al menos ${BUSINESS_RULES.PASSWORD.MIN_LENGTH} caracteres`,
    };
  }

  if (!BUSINESS_RULES.PASSWORD.PATTERN.test(password)) {
    return {
      isValid: false,
      message: 'La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial',
    };
  }

  return { isValid: true, message: 'Contraseña válida' };
}

/**
 * Validate topup amount
 * @param amount - Amount to validate
 * @returns Object with validation result and message
 */
export function validateTopupAmount(amount: number): { isValid: boolean; message: string } {
  if (amount < BUSINESS_RULES.TOPUP.MIN_VALUE) {
    return {
      isValid: false,
      message: `El valor mínimo es ${formatCurrency(BUSINESS_RULES.TOPUP.MIN_VALUE)}`,
    };
  }

  if (amount > BUSINESS_RULES.TOPUP.MAX_VALUE) {
    return {
      isValid: false,
      message: `El valor máximo es ${formatCurrency(BUSINESS_RULES.TOPUP.MAX_VALUE)}`,
    };
  }

  return { isValid: true, message: 'Valor válido' };
}

/**
 * Debounce function
 * @param func - Function to debounce
 * @param delay - Delay in milliseconds
 * @returns Debounced function
 */
export function debounce<T extends (...args: any[]) => any>(func: T, delay: number): (...args: Parameters<T>) => void {
  let timeoutId: NodeJS.Timeout;

  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delay);
  };
}

/**
 * Throttle function
 * @param func - Function to throttle
 * @param delay - Delay in milliseconds
 * @returns Throttled function
 */
export function throttle<T extends (...args: any[]) => any>(func: T, delay: number): (...args: Parameters<T>) => void {
  let inThrottle: boolean;

  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), delay);
    }
  };
}

/**
 * Safe JSON parse with fallback
 * @param jsonString - JSON string to parse
 * @param fallback - Fallback value if parsing fails
 * @returns Parsed object or fallback
 */
export function safeJsonParse<T>(jsonString: string | null, fallback: T): T {
  if (!jsonString) return fallback;

  try {
    return JSON.parse(jsonString) as T;
  } catch {
    return fallback;
  }
}

/**
 * Generate random ID
 * @param prefix - Optional prefix for the ID
 * @returns Random ID string
 */
export function generateId(prefix = ''): string {
  const timestamp = Date.now().toString(36);
  const randomPart = Math.random().toString(36).substring(2, 15);
  return `${prefix}${prefix ? '_' : ''}${timestamp}_${randomPart}`;
}

/**
 * Capitalize first letter of a string
 * @param str - String to capitalize
 * @returns Capitalized string
 */
export function capitalize(str: string): string {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

/**
 * Convert transaction status to display text
 * @param status - Transaction status
 * @returns Display text in Spanish
 */
export function getStatusDisplayText(status: string): string {
  const statusMap: Record<string, string> = {
    PENDING: 'Pendiente',
    SUCCESS: 'Exitosa',
    FAILED: 'Fallida',
    CANCELLED: 'Cancelada',
  };

  return statusMap[status] || status;
}

/**
 * Get status color class for styling
 * @param status - Transaction status
 * @returns CSS class names for status styling
 */
export function getStatusColorClass(status: string): string {
  const colorMap: Record<string, string> = {
    PENDING: 'text-yellow-600 bg-yellow-50',
    SUCCESS: 'text-green-600 bg-green-50',
    FAILED: 'text-red-600 bg-red-50',
    CANCELLED: 'text-gray-600 bg-gray-50',
  };

  return colorMap[status] || 'text-gray-600 bg-gray-50';
}
