import { Smartphone, History, CreditCard, Building2, LogOut, Menu } from 'lucide-react';
import { Button } from '../ui/button';
import { Card } from '../ui/card';
import { Badge } from '../ui/badge';
import { Separator } from '../ui/separator';
import { Sheet, SheetContent, SheetTrigger } from '../ui/sheet';
import { toast } from 'sonner';
import { GRADIENTS } from '../../lib';

interface SidebarProps {
  currentModule: string;
  onModuleChange: (module: string) => void;
  onLogout: () => void;
}

const modules = [
  { id: 'topup', name: 'Recargas Móviles', icon: Smartphone, description: 'Recargas a operadores' },
  { id: 'history', name: 'Historial', icon: History, description: 'Transacciones realizadas' },
  { id: 'pins', name: 'Compra de Pines', icon: CreditCard, description: 'Pines y tarjetas', badge: 'Próximamente' },
  {
    id: 'banking',
    name: 'Transacciones Bancarias',
    icon: Building2,
    description: 'Operaciones bancarias',
    badge: 'Próximamente',
  },
];

export function Sidebar({ currentModule, onModuleChange, onLogout }: SidebarProps) {
  const handleLogoutClick = () => {
    toast('¿Cerrar sesión?', {
      description: 'Tendrás que iniciar sesión nuevamente.',
      action: {
        label: 'Cerrar Sesión',
        onClick: () => onLogout(),
      },
      cancel: {
        label: 'Cancelar',
        onClick: () => {},
      },
      duration: 10000,
      position: 'bottom-center',
      classNames: {
        toast: 'border-slate-200 bg-white shadow-xl',
        title: 'text-slate-800 font-semibold',
        description: 'text-slate-600',
        actionButton:
          '!bg-gradient-to-r !from-red-500 !to-pink-500 !text-white hover:!from-red-600 hover:!to-pink-600 !border-0 !shadow-md !rounded-lg !font-medium !transition-all !duration-200 hover:!shadow-lg',
        cancelButton: ' !rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 border border-slate-300',
      },
    });
  };

  const SidebarContent = () => (
    <div className="flex flex-col h-full">
      <div className={`p-6 ${GRADIENTS.primary}`}>
        <div className="flex items-center space-x-2">
          <div className="w-8 h-8 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center border border-white/20">
            <span className="text-white font-bold text-sm">MP</span>
          </div>
          <div>
            <h2 className="text-lg font-semibold text-white">MultiPagos</h2>
            <p className="text-xs text-blue-100">Portal Transaccional</p>
          </div>
        </div>
      </div>

      <nav className="flex-1 p-4 space-y-2">
        {modules.map((module) => {
          const Icon = module.icon;
          const isActive = currentModule === module.id;
          const isDisabled = module.badge === 'Próximamente';

          return (
            <Button
              key={module.id}
              variant={isActive ? 'default' : 'ghost'}
              size="default"
              className={`w-full justify-start h-auto p-3 transition-all duration-200 cursor-pointer ${
                isActive
                  ? `${GRADIENTS.primary} text-white shadow-lg border border-blue-200/50 ${GRADIENTS.primaryHover}`
                  : 'hover:bg-slate-50 hover:text-slate-700 text-slate-600 border border-transparent hover:border-slate-200'
              } ${isDisabled ? 'opacity-50 cursor-not-allowed' : ''}`}
              onClick={() => !isDisabled && onModuleChange(module.id)}
              disabled={isDisabled}
            >
              <div className="flex items-center space-x-3 w-full">
                <Icon className="h-5 w-5 flex-shrink-0" />
                <div className="flex-1 text-left">
                  <div className="flex items-center justify-between">
                    <span className="font-medium">{module.name}</span>
                    {module.badge && (
                      <Badge variant="secondary" className="text-xs ml-2">
                        {module.badge}
                      </Badge>
                    )}
                  </div>
                  <p className={`text-xs mt-0.5 ${isActive ? 'text-blue-100' : 'text-slate-500'}`}>
                    {module.description}
                  </p>
                </div>
              </div>
            </Button>
          );
        })}
      </nav>

      <Separator />

      <div className="p-4">
        <Button
          variant="ghost"
          size="default"
          className="w-full justify-start h-auto p-3 text-red-600 bg-red-50 border border-red-200 hover:text-red-700 hover:bg-red-100 hover:border-red-300 transition-all duration-200 cursor-pointer"
          onClick={handleLogoutClick}
        >
          <LogOut className="h-5 w-5 mr-3" />
          <span className="font-medium">Cerrar Sesión</span>
        </Button>
      </div>
    </div>
  );

  return (
    <>
      <div className="hidden lg:flex lg:w-80 lg:flex-col h-screen">
        <Card className="h-full border-r overflow-hidden !p-0 !rounded-none border-l-0 !border-t-0">
          <SidebarContent />
        </Card>
      </div>

      <div className="lg:hidden">
        <Sheet>
          <SheetTrigger asChild>
            <Button variant="outline" size="icon" className="fixed top-4 left-4 z-50">
              <Menu className="h-4 w-4" />
            </Button>
          </SheetTrigger>
          <SheetContent side="left" className="w-80 p-0">
            <SidebarContent />
          </SheetContent>
        </Sheet>
      </div>
    </>
  );
}
