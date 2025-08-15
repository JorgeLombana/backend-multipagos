/**
 * Theme constants for consistent styling across the application
 */

// Gradient definitions for reuse across components
export const GRADIENTS = {
  primary: 'bg-gradient-to-r from-blue-500 to-purple-600',
  primaryHover: 'hover:from-blue-600 hover:to-purple-700',
  success: 'bg-gradient-to-r from-green-500 to-emerald-500',
  successHover: 'hover:from-green-600 hover:to-emerald-600',
  warning: 'bg-gradient-to-r from-yellow-500 to-orange-500',
  info: 'bg-gradient-to-r from-blue-500 to-cyan-500',
  secondary: 'bg-gradient-to-r from-gray-500 to-slate-500',
  accent: 'bg-gradient-to-r from-indigo-500 to-purple-600',
  danger: 'bg-gradient-to-r from-red-500 to-pink-500',

  // Light versions for cards and backgrounds
  primaryLight: 'bg-gradient-to-br from-blue-50 to-purple-50',
  primaryPastel: 'bg-gradient-to-br from-blue-50/30 via-purple-50/20 to-indigo-50/30',
  successLight: 'bg-gradient-to-br from-green-50 to-emerald-50',
  warningLight: 'bg-gradient-to-br from-yellow-50 to-orange-50',
  infoLight: 'bg-gradient-to-br from-blue-50 to-cyan-50',
  background: 'bg-gradient-to-br from-blue-100/60 via-purple-100/50 to-indigo-200/60',
} as const;

// Color mappings for consistency
export const COLORS = {
  suppliers: {
    '8753': 'bg-red-500', // Claro - fallback color
    '9773': 'bg-green-500', // Movistar - fallback color
    '3398': 'bg-blue-500', // Tigo - fallback color
    '4689': 'bg-purple-500', // WOM - fallback color
  },
  status: {
    success: 'text-green-500',
    error: 'text-red-500',
    warning: 'text-yellow-500',
    info: 'text-blue-500',
  },
} as const;

// Supplier image mappings
export const SUPPLIER_IMAGES = {
  '8753': '/claro.png', // Claro
  '9773': '/movistar.png', // Movistar
  '3398': '/tigo.png', // Tigo
  '4689': '/wom.png', // WOM
} as const;

// Common shadow and border styles
export const SHADOWS = {
  card: 'shadow-lg',
  cardHover: 'hover:shadow-xl',
  small: 'shadow-sm',
  medium: 'shadow-md',
  large: 'shadow-xl',
} as const;

// Animation classes
export const ANIMATIONS = {
  spin: 'animate-spin',
  pulse: 'animate-pulse',
  bounce: 'animate-bounce',
  scale: 'hover:scale-105',
  scaleSmall: 'hover:scale-102',
} as const;

// Common spacing and sizing
export const SPACING = {
  cardPadding: 'p-4',
  sectionGap: 'space-y-6',
  itemGap: 'space-y-4',
  smallGap: 'space-y-2',
} as const;
