import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from '@/contexts';
import { ROUTES } from '@/lib/constants';
import { RegisterPage, LoginPage, DashboardPage } from './pages';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-background">
          <Routes>
            {/* Default redirect to register */}
            <Route path="/" element={<Navigate to={ROUTES.REGISTER} replace />} />
            
            {/* Auth routes */}
            <Route path={ROUTES.REGISTER} element={<RegisterPage />} />
            <Route path={ROUTES.LOGIN} element={<LoginPage />} />
            
            {/* Protected routes */}
            <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
            
            {/* Catch all - redirect to register */}
            <Route path="*" element={<Navigate to={ROUTES.REGISTER} replace />} />
          </Routes>
          
          {/* Global toast notifications */}
          <Toaster 
            position="top-right"
            toastOptions={{
              duration: 4000,
            }}
          />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
