import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Loader2, User, Mail, Lock } from 'lucide-react';
import toast from 'react-hot-toast';

import { useAuth } from '@/contexts';
import { registerFormSchema, type RegisterFormData } from '@/lib/schemas';
import { ROUTES } from '@/lib/constants';

import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const { register: registerUser, isLoading } = useAuth();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerFormSchema),
    defaultValues: {
      name: '',
      email: '',
      password: '',
      confirmPassword: '',
    },
  });

  const onSubmit = async (data: RegisterFormData) => {
    try {
      const response = await registerUser({
        name: data.name,
        email: data.email,
        password: data.password,
      });

      const successMessage = response?.message || 'Usuario registrado exitosamente';
      toast.success(successMessage);
      reset();
      navigate(ROUTES.LOGIN);
    } catch (error: any) {
      let errorMessage = 'Error al registrar usuario';

      if (error?.message) {
        errorMessage = error.message;
      } else if (error?.error) {
        errorMessage = error.error;
      } else if (error?.data?.message) {
        errorMessage = error.data.message;
      }

      toast.error(errorMessage);
      console.error('Registration error:', error);
    }
  };

  const isFormLoading = isLoading || isSubmitting;

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-pink-50 to-fuchsia-100 p-4">
      <Card className="w-full max-w-md shadow-xl border-0">
        <CardHeader className="space-y-1 justify-items-center py-4">
          <CardTitle className="text-2xl font-bold text-gray-900">Multi-Pagos</CardTitle>
          <p className="text-gray-600">Ingresa tus datos para registrarte en Multi-Pagos</p>
        </CardHeader>

        <div className="p-6 space-y-4">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {/* Name Field */}
            <div className="space-y-2">
              <Label htmlFor="name">Nombre completo</Label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <Input
                  id="name"
                  type="text"
                  placeholder="Ingresa tu nombre completo"
                  className="pl-10"
                  {...register('name')}
                  disabled={isFormLoading}
                />
              </div>
              {errors.name && <p className="text-sm text-red-600">{errors.name.message}</p>}
            </div>

            {/* Email Field */}
            <div className="space-y-2">
              <Label htmlFor="email">Correo electrónico</Label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <Input
                  id="email"
                  type="email"
                  placeholder="correo@ejemplo.com"
                  className="pl-10"
                  {...register('email')}
                  disabled={isFormLoading}
                />
              </div>
              {errors.email && <p className="text-sm text-red-600">{errors.email.message}</p>}
            </div>

            {/* Password Field */}
            <div className="space-y-2">
              <Label htmlFor="password">Contraseña</Label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <Input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Crea una contraseña segura"
                  className="pl-10 pr-10"
                  {...register('password')}
                  disabled={isFormLoading}
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                  onClick={() => setShowPassword(!showPassword)}
                  disabled={isFormLoading}
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              {errors.password && <p className="text-sm text-red-600">{errors.password.message}</p>}
            </div>

            {/* Confirm Password Field */}
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirmar contraseña</Label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                <Input
                  id="confirmPassword"
                  type={showConfirmPassword ? 'text' : 'password'}
                  placeholder="Confirma tu contraseña"
                  className="pl-10 pr-10"
                  {...register('confirmPassword')}
                  disabled={isFormLoading}
                />
                <button
                  type="button"
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  disabled={isFormLoading}
                >
                  {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              {errors.confirmPassword && <p className="text-sm text-red-600">{errors.confirmPassword.message}</p>}
            </div>

            <Button
              type="submit"
              className="w-full bg-gradient-to-r from-pink-500 to-fuchsia-600 hover:from-pink-600 hover:to-fuchsia-700 text-white"
              disabled={isFormLoading}
            >
              {isFormLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Registrando...
                </>
              ) : (
                'Crear cuenta'
              )}
            </Button>

            <div className="text-center text-sm text-gray-600">
              ¿Ya tienes cuenta?{' '}
              <Link to={ROUTES.LOGIN} className="text-pink-600 hover:text-fuchsia-500 font-medium">
                Inicia sesión
              </Link>
            </div>
          </form>
        </div>
      </Card>
    </div>
  );
}

export default RegisterPage;
