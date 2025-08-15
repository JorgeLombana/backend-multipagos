import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from '@/contexts';
import { ProtectedRoute, PublicRoute } from '@/components/auth';
import { ROUTES } from '@/lib/constants';
import { RegisterPage, LoginPage, DashboardPage } from './pages';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-background">
          <Routes>
            {/* Default redirect to dashboard if authenticated, otherwise to login */}
            <Route path="/" element={<Navigate to={ROUTES.DASHBOARD} replace />} />

            {/* Public routes (redirect to dashboard if already authenticated) */}
            <Route
              path={ROUTES.REGISTER}
              element={
                <PublicRoute>
                  <RegisterPage />
                </PublicRoute>
              }
            />
            <Route
              path={ROUTES.LOGIN}
              element={
                <PublicRoute>
                  <LoginPage />
                </PublicRoute>
              }
            />

            {/* Protected routes (require authentication) */}
            <Route
              path={ROUTES.DASHBOARD}
              element={
                <ProtectedRoute>
                  <DashboardPage />
                </ProtectedRoute>
              }
            />

            {/* Catch all - redirect to dashboard */}
            <Route path="*" element={<Navigate to={ROUTES.DASHBOARD} replace />} />
          </Routes>

          {/* Global toast notifications */}
          <Toaster position="top-right" richColors expand={true} />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
