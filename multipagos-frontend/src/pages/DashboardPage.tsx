import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'sonner';
import { Sidebar } from '../components/features/Sidebar';
import { TopupModule } from '../components/features/TopupModule';
import { HistoryModule } from '../components/features/HistoryModule';
import { GRADIENTS } from '../lib';

function DashboardPage() {
  const [currentModule, setCurrentModule] = useState('topup');
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    try {
      logout();
      toast.success('Sesión cerrada correctamente');
      navigate('/login');
    } catch (error) {
      console.error('Error during logout:', error);
      toast.error('Error al cerrar sesión');
    }
  };

  const renderModule = () => {
    switch (currentModule) {
      case 'topup':
        return <TopupModule />;
      case 'history':
        return <HistoryModule />;
      default:
        return <TopupModule />;
    }
  };

  return (
    <div className={`min-h-screen ${GRADIENTS.background} flex flex-col`}>
      <div className="flex flex-1">
        <Sidebar currentModule={currentModule} onModuleChange={setCurrentModule} onLogout={handleLogout} />

        <div className="flex-1 lg:ml-0 flex flex-col">
          <main className="flex-1 p-6 lg:p-8">{renderModule()}</main>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
