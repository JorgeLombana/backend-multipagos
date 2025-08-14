import { z } from 'zod';
import { BUSINESS_RULES, VALID_SUPPLIER_IDS } from './constants';

const emailSchema = z
  .string()
  .min(1, 'El email es requerido')
  .email('Formato de email inválido')
  .max(255, 'El email es demasiado largo');

const passwordSchema = z
  .string()
  .min(
    BUSINESS_RULES.PASSWORD.MIN_LENGTH,
    `La contraseña debe tener al menos ${BUSINESS_RULES.PASSWORD.MIN_LENGTH} caracteres`
  )
  .regex(
    BUSINESS_RULES.PASSWORD.PATTERN,
    'La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial'
  );

const nameSchema = z
  .string()
  .min(BUSINESS_RULES.NAME.MIN_LENGTH, `El nombre debe tener al menos ${BUSINESS_RULES.NAME.MIN_LENGTH} caracteres`)
  .max(BUSINESS_RULES.NAME.MAX_LENGTH, `El nombre no puede tener más de ${BUSINESS_RULES.NAME.MAX_LENGTH} caracteres`)
  .regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/, 'El nombre solo puede contener letras y espacios');

const phoneSchema = z
  .string()
  .min(1, 'El número de celular es requerido')
  .length(10, 'El número debe tener exactamente 10 dígitos')
  .regex(BUSINESS_RULES.TOPUP.PHONE_PATTERN, 'El número debe empezar con 3 y contener solo dígitos');

export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, 'La contraseña es requerida'),
});

export const registerSchema = z.object({
  name: nameSchema,
  email: emailSchema,
  password: passwordSchema,
});

export const registerFormSchema = registerSchema
  .extend({
    confirmPassword: z.string().min(1, 'Confirme su contraseña'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Las contraseñas no coinciden',
    path: ['confirmPassword'],
  });

export const topupSchema = z.object({
  cellPhone: phoneSchema,
  value: z
    .number()
    .min(BUSINESS_RULES.TOPUP.MIN_VALUE, `El valor mínimo es $${BUSINESS_RULES.TOPUP.MIN_VALUE.toLocaleString()}`)
    .max(BUSINESS_RULES.TOPUP.MAX_VALUE, `El valor máximo es $${BUSINESS_RULES.TOPUP.MAX_VALUE.toLocaleString()}`)
    .int('El valor debe ser un número entero')
    .positive('El valor debe ser positivo'),
  supplierId: z
    .string()
    .min(1, 'Seleccione un proveedor')
    .refine(
      (id): id is (typeof VALID_SUPPLIER_IDS)[number] => VALID_SUPPLIER_IDS.includes(id as any),
      'Proveedor no válido'
    ),
});

export const topupFormSchema = z.object({
  cellPhone: phoneSchema,
  value: z
    .string()
    .min(1, 'El valor es requerido')
    .regex(/^\d+$/, 'Solo se permiten números')
    .transform((val) => parseInt(val, 10))
    .refine(
      (num) => num >= BUSINESS_RULES.TOPUP.MIN_VALUE,
      `El valor mínimo es $${BUSINESS_RULES.TOPUP.MIN_VALUE.toLocaleString()}`
    )
    .refine(
      (num) => num <= BUSINESS_RULES.TOPUP.MAX_VALUE,
      `El valor máximo es $${BUSINESS_RULES.TOPUP.MAX_VALUE.toLocaleString()}`
    ),
  supplierId: z
    .string()
    .min(1, 'Seleccione un proveedor')
    .refine(
      (id): id is (typeof VALID_SUPPLIER_IDS)[number] => VALID_SUPPLIER_IDS.includes(id as any),
      'Proveedor no válido'
    ),
});

export const paginationSchema = z.object({
  page: z
    .number()
    .int('La página debe ser un número entero')
    .min(0, 'La página no puede ser negativa')
    .default(BUSINESS_RULES.PAGINATION.DEFAULT_PAGE),
  size: z
    .number()
    .int('El tamaño debe ser un número entero')
    .min(1, 'El tamaño mínimo es 1')
    .max(BUSINESS_RULES.PAGINATION.MAX_SIZE, `El tamaño máximo es ${BUSINESS_RULES.PAGINATION.MAX_SIZE}`)
    .default(BUSINESS_RULES.PAGINATION.DEFAULT_SIZE),
  sortField: z.string().default(BUSINESS_RULES.PAGINATION.DEFAULT_SORT_FIELD),
  sortDirection: z.enum(['ASC', 'DESC']).default(BUSINESS_RULES.PAGINATION.DEFAULT_SORT_DIRECTION),
});

export const updateProfileSchema = z.object({
  name: nameSchema,
  email: emailSchema,
});

export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, 'La contraseña actual es requerida'),
    newPassword: passwordSchema,
    confirmNewPassword: z.string().min(1, 'Confirme su nueva contraseña'),
  })
  .refine((data) => data.newPassword === data.confirmNewPassword, {
    message: 'Las contraseñas no coinciden',
    path: ['confirmNewPassword'],
  });

export const transactionFilterSchema = z
  .object({
    startDate: z.string().optional(),
    endDate: z.string().optional(),
    status: z.enum(['PENDING', 'SUCCESS', 'FAILED', 'CANCELLED']).optional(),
    supplierId: z.string().optional(),
    minAmount: z.number().min(0).optional(),
    maxAmount: z.number().min(0).optional(),
  })
  .refine(
    (data) => {
      if (data.minAmount && data.maxAmount) {
        return data.minAmount <= data.maxAmount;
      }
      return true;
    },
    {
      message: 'El monto mínimo no puede ser mayor al máximo',
      path: ['maxAmount'],
    }
  );

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerFormSchema>;
export type TopupFormData = z.infer<typeof topupFormSchema>;
export type PaginationParams = z.infer<typeof paginationSchema>;
export type UpdateProfileData = z.infer<typeof updateProfileSchema>;
export type ChangePasswordData = z.infer<typeof changePasswordSchema>;
export type TransactionFilterData = z.infer<typeof transactionFilterSchema>;
