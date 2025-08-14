export const API_CONFIG = {
  BASE_URL: 'http://localhost:8080/api/v1',
  TIMEOUT: 10000,
  VERSION: 'v1',
} as const;

export const STORAGE_KEYS = {
  AUTH_TOKEN: 'auth_token',
  AUTH_USER: 'auth_user',
} as const;

export const CACHE_CONFIG = {
  SUPPLIERS_DURATION: 5 * 60 * 1000, // 5 minutes
} as const;

export const BUSINESS_RULES = {
  TOPUP: {
    MIN_VALUE: 1000,
    MAX_VALUE: 100000,
    PHONE_PATTERN: /^3\d{9}$/,
    PHONE_LENGTH: 10,
  },
  PAGINATION: {
    DEFAULT_PAGE: 0,
    DEFAULT_SIZE: 20,
    MAX_SIZE: 100,
    DEFAULT_SORT_FIELD: 'createdAt',
    DEFAULT_SORT_DIRECTION: 'DESC' as const,
  },
  PASSWORD: {
    MIN_LENGTH: 8,
    PATTERN: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&].*$/,
  },
  NAME: {
    MIN_LENGTH: 2,
    MAX_LENGTH: 100,
  },
} as const;

export const SUPPLIERS = {
  CLARO: '8753',
  MOVISTAR: '9773',
  TIGO: '3398',
  WOM: '4689',
} as const;

export const VALID_SUPPLIER_IDS: readonly string[] = Object.values(SUPPLIERS);

export const TRANSACTION_STATUS = {
  PENDING: 'PENDING',
  SUCCESS: 'SUCCESS',
  FAILED: 'FAILED',
  CANCELLED: 'CANCELLED',
} as const;

export type TransactionStatus = (typeof TRANSACTION_STATUS)[keyof typeof TRANSACTION_STATUS];

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  TOPUP: '/dashboard/topup',
  HISTORY: '/dashboard/history',
  PROFILE: '/dashboard/profile',
} as const;

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  UNPROCESSABLE_ENTITY: 422,
  INTERNAL_SERVER_ERROR: 500,
} as const;

export const ERROR_MESSAGES = {
  NETWORK: 'No se pudo conectar al servidor. Verifique su conexión a internet.',
  SESSION_EXPIRED: 'Su sesión ha expirado. Por favor, inicie sesión nuevamente.',
  UNAUTHORIZED: 'No tiene permisos para realizar esta acción.',
  NOT_FOUND: 'El recurso solicitado no fue encontrado.',
  VALIDATION: 'Los datos enviados no son válidos.',
  SERVER_ERROR: 'Error interno del servidor. Intente nuevamente más tarde.',
  UNKNOWN: 'Error inesperado. Intente nuevamente.',
} as const;

export const SUCCESS_MESSAGES = {
  LOGIN: 'Inicio de sesión exitoso',
  REGISTER: 'Registro exitoso',
  TOPUP: 'Recarga procesada exitosamente',
  LOGOUT: 'Sesión cerrada exitosamente',
} as const;

export const UI_CONFIG = {
  TOAST_DURATION: 5000,
  DEBOUNCE_DELAY: 300,
  ANIMATION_DURATION: 200,
} as const;

export const PATTERNS = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PHONE: /^3\d{9}$/,
  CURRENCY: /^\d+$/,
} as const;
