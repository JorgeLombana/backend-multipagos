import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Mail, Lock, Eye, EyeOff } from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import { ROUTES } from '@/lib/constants';
import { loginSchema, type LoginFormData } from '@/lib/schemas';
import { useAuth } from '@/contexts';
import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

function LoginPage() {
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);

  const form = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      const response = await login(data);
      const successMessage = response?.message || 'Inicio de sesión exitoso';
      toast.success(successMessage);
      form.reset();
      navigate(ROUTES.DASHBOARD);
    } catch (error: any) {
      let errorMessage = 'Error al iniciar sesión';

      if (error?.message) {
        errorMessage = error.message;
      } else if (error?.error) {
        errorMessage = error.error;
      } else if (error?.data?.message) {
        errorMessage = error.data.message;
      }

      toast.error(errorMessage);
      console.error('Login error:', error);
    }
  };

  const isFormLoading = isLoading || form.formState.isSubmitting;

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-pink-50 to-fuchsia-100 p-4">
      <Card className="w-full max-w-md shadow-xl border-0">
        <CardHeader className="space-y-1 justify-items-center py-4">
          <CardTitle className="text-2xl font-bold text-gray-900">Multi-Pagos</CardTitle>
          <p className="text-gray-600">Accede a tu cuenta de Multi-Pagos</p>
        </CardHeader>

        <div className="p-6 space-y-4">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              {/* Email Field */}
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Correo electrónico</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                        <Input
                          type="email"
                          placeholder="correo@ejemplo.com"
                          className="pl-10"
                          disabled={isFormLoading}
                          {...field}
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Password Field */}
              <FormField
                control={form.control}
                name="password"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Contraseña</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                        <Input
                          type={showPassword ? 'text' : 'password'}
                          placeholder="Ingresa tu contraseña"
                          className="pl-10 pr-10"
                          disabled={isFormLoading}
                          {...field}
                        />
                        <button
                          type="button"
                          onClick={() => setShowPassword(!showPassword)}
                          className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                          disabled={isFormLoading}
                        >
                          {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                        </button>
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Submit Button */}
              <Button
                type="submit"
                className="w-full bg-gradient-to-r from-pink-500 to-fuchsia-600 hover:from-pink-600 hover:to-fuchsia-700 text-white font-medium py-2 px-4 rounded-md transition-all duration-200"
                disabled={isFormLoading}
              >
                {isFormLoading ? 'Iniciando sesión...' : 'Iniciar sesión'}
              </Button>
            </form>
          </Form>

          {/* Register Link */}
          <div className="text-center text-sm text-gray-600">
            ¿No tienes cuenta?{' '}
            <Link to={ROUTES.REGISTER} className="text-pink-600 hover:text-fuchsia-500 font-medium transition-colors">
              Regístrate aquí
            </Link>
          </div>
        </div>
      </Card>
    </div>
  );
}

export default LoginPage;
