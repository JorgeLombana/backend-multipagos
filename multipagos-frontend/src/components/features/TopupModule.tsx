import { Card, CardContent } from '../ui/card';
import { GradientCard } from '../ui/gradient-card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Badge } from '../ui/badge';
import { Smartphone, Zap, DollarSign, AlertCircle, CheckCircle, Loader2 } from 'lucide-react';
import { useState, useEffect } from 'react';
import { topupService } from '@/services/topup';
import { toast } from 'sonner';
import { TopupTicketDialog } from './TopupTicketDialog';
import {
  GRADIENTS,
  COLORS,
  SUPPLIER_IMAGES,
  ANIMATIONS,
  TOPUP_LIMITS,
  QUICK_AMOUNTS,
  formatCurrency,
  isValidPhone,
  extractErrorMessage,
} from '@/lib';
import type { TopupRequest } from '@/types';

interface Supplier {
  id: string;
  name: string;
  color: string;
  image: string;
}

export function TopupModule() {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [supplier, setSupplier] = useState('');
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [isLoadingSuppliers, setIsLoadingSuppliers] = useState(true);
  const [isProcessingTopup, setIsProcessingTopup] = useState(false);
  const [showTicketDialog, setShowTicketDialog] = useState(false);
  const [ticketData, setTicketData] = useState<any>(null);

  useEffect(() => {
    loadSuppliers();
  }, []);

  const loadSuppliers = async () => {
    try {
      setIsLoadingSuppliers(true);
      const response = await topupService.getSuppliers();

      if (response.status === 'success' && response.data) {
        const suppliersWithColors = response.data.map((sup: any) => ({
          id: sup.id,
          name: sup.name,
          color: COLORS.suppliers[sup.id as keyof typeof COLORS.suppliers] || 'bg-gray-500',
          image: SUPPLIER_IMAGES[sup.id as keyof typeof SUPPLIER_IMAGES] || '',
        }));
        setSuppliers(suppliersWithColors);
      }
    } catch (error: any) {
      const message = extractErrorMessage(error);
      toast.error(`Error al cargar los proveedores: ${message}`);
    } finally {
      setIsLoadingSuppliers(false);
    }
  };

  const handleTopupSubmit = async () => {
    try {
      if (!phoneNumber || !amount || !supplier) {
        toast.error('Por favor complete todos los campos');
        return;
      }

      if (!isValidPhone(phoneNumber)) {
        toast.error(
          `El número debe empezar con ${TOPUP_LIMITS.PHONE_PREFIX} y tener exactamente ${TOPUP_LIMITS.PHONE_LENGTH} dígitos`
        );
        return;
      }

      const numericAmount = parseFloat(amount);
      if (numericAmount < TOPUP_LIMITS.MIN_AMOUNT || numericAmount > TOPUP_LIMITS.MAX_AMOUNT) {
        toast.error(
          `El monto debe estar entre ${formatCurrency(TOPUP_LIMITS.MIN_AMOUNT)} y ${formatCurrency(
            TOPUP_LIMITS.MAX_AMOUNT
          )}`
        );
        return;
      }

      setIsProcessingTopup(true);

      const topupData: TopupRequest = {
        cellPhone: phoneNumber,
        value: numericAmount,
        supplierId: supplier,
      };

      const response = await topupService.processTopup(topupData);

      if (response.status === 'success') {
        setTicketData(response.data);
        setShowTicketDialog(true);

        setPhoneNumber('');
        setAmount('');
        setSupplier('');

        toast.success('¡Recarga procesada exitosamente!');
      } else {
        toast.error(response.message || 'Error al procesar la recarga');
      }
    } catch (error: any) {
      const message = extractErrorMessage(error);
      toast.error(`Error al procesar la recarga: ${message}`);
    } finally {
      setIsProcessingTopup(false);
    }
  };

  const isFormValid = phoneNumber && amount && supplier && !isProcessingTopup;

  return (
    <div className="space-y-6">
      <div className="grid gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2">
          <GradientCard
            title="Nueva Recarga"
            description="Completa los datos para realizar la recarga móvil"
            icon={Smartphone}
            gradient={GRADIENTS.primary}
          >
            <div className="space-y-3">
              <div className="space-y-1">
                <Label htmlFor="phone" className="text-sm text-slate-700">
                  Número de Teléfono
                </Label>
                <Input
                  id="phone"
                  type="tel"
                  placeholder={`${TOPUP_LIMITS.PHONE_PREFIX}XX XXX XXXX`}
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  maxLength={TOPUP_LIMITS.PHONE_LENGTH}
                  className="h-9"
                />
                <p className="text-xs text-slate-600">
                  Debe iniciar en {TOPUP_LIMITS.PHONE_PREFIX} y tener {TOPUP_LIMITS.PHONE_LENGTH} dígitos
                </p>
              </div>

              <div className="space-y-2">
                <Label className="text-sm text-slate-700">Operador</Label>
                {isLoadingSuppliers ? (
                  <div className="flex items-center justify-center p-8">
                    <Loader2 className="h-6 w-6 animate-spin text-slate-500" />
                    <span className="ml-2 text-sm text-slate-600">Cargando proveedores...</span>
                  </div>
                ) : suppliers.length > 0 ? (
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                    {suppliers.map((sup) => (
                      <Card
                        key={sup.id}
                        className={`cursor-pointer transition-all duration-200 hover:shadow-lg hover:scale-[1.02] border-2 rounded-xl ${
                          supplier === sup.id
                            ? 'border-slate-400 ring-2 ring-slate-300 shadow-lg bg-slate-50'
                            : 'border-slate-200 hover:border-slate-300 hover:bg-slate-25'
                        }`}
                        onClick={() => setSupplier(sup.id)}
                      >
                        <CardContent className=" text-center">
                          {sup.image ? (
                            <div className="w-24 h-24 mx-auto mb-4 flex items-center justify-center">
                              <img
                                src={sup.image}
                                alt={sup.name}
                                className="w-full h-full object-contain"
                                onError={(e) => {
                                  e.currentTarget.style.display = 'none';
                                  const fallback = e.currentTarget.parentElement?.nextElementSibling as HTMLElement;
                                  if (fallback) fallback.style.display = 'block';
                                }}
                              />
                            </div>
                          ) : null}
                          <div
                            className={`w-16 h-16 ${sup.color} rounded-full mx-auto mb-4 shadow-md ${
                              sup.image ? 'hidden' : 'block'
                            }`}
                            style={{ display: sup.image ? 'none' : 'block' }}
                          ></div>
                          <p className="font-semibold text-lg text-slate-700">{sup.name}</p>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                ) : (
                  <div className="flex items-center justify-center p-8 text-center">
                    <div>
                      <AlertCircle className="h-8 w-8 mx-auto mb-2 text-yellow-500" />
                      <p className="text-sm text-slate-600">No se pudieron cargar los proveedores.</p>
                      <Button variant="outline" size="sm" onClick={loadSuppliers} className="mt-2">
                        Reintentar
                      </Button>
                    </div>
                  </div>
                )}
              </div>

              <div className="space-y-2">
                <Label className="text-sm text-slate-700">Monto de Recarga</Label>
                <div className="grid grid-cols-3 md:grid-cols-6 gap-2 mb-2">
                  {QUICK_AMOUNTS.map((quickAmount) => (
                    <Button
                      key={quickAmount}
                      variant={amount === quickAmount.toString() ? 'default' : 'outline'}
                      size="sm"
                      onClick={() => setAmount(quickAmount.toString())}
                      className={`text-sm transition-all ${
                        amount === quickAmount.toString()
                          ? `${GRADIENTS.success} text-white shadow-lg`
                          : 'hover:bg-green-50 hover:border-green-300 hover:text-green-700'
                      }`}
                    >
                      {formatCurrency(quickAmount)}
                    </Button>
                  ))}
                </div>
                <Input
                  type="number"
                  placeholder="Monto personalizado"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  min={TOPUP_LIMITS.MIN_AMOUNT}
                  max={TOPUP_LIMITS.MAX_AMOUNT}
                  className="h-9"
                />
                <p className="text-xs text-slate-600">
                  Mínimo: {formatCurrency(TOPUP_LIMITS.MIN_AMOUNT)} - Máximo: {formatCurrency(TOPUP_LIMITS.MAX_AMOUNT)}
                </p>
              </div>
              <Button
                className={`w-full ${GRADIENTS.primary} shadow-lg`}
                size="default"
                disabled={!isFormValid}
                onClick={handleTopupSubmit}
              >
                {isProcessingTopup ? (
                  <>
                    <Loader2 className={`h-4 w-4 mr-2 ${ANIMATIONS.spin}`} />
                    Procesando Recarga...
                  </>
                ) : (
                  <>
                    <Zap className="h-4 w-4 mr-2" />
                    Realizar Recarga
                  </>
                )}
              </Button>
              {!isFormValid && !isProcessingTopup && (
                <p className="text-center text-sm text-slate-600">Complete todos los campos para continuar</p>
              )}
              {isProcessingTopup && (
                <div className="flex items-center justify-center">
                  <CheckCircle className="h-4 w-4 mr-2 text-green-500" />
                  <p className="text-center text-sm text-green-600">Conectando con API de Puntored...</p>
                </div>
              )}
            </div>
          </GradientCard>
        </div>

        <div>
          <GradientCard title="Resumen de Recarga" gradient={GRADIENTS.success}>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-xs text-gray-600 font-medium">Teléfono:</span>
                <span className="text-xs font-semibold text-gray-900">{phoneNumber || 'No ingresado'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-xs text-gray-600 font-medium">Operador:</span>
                <span className="text-xs font-semibold text-gray-900">
                  {supplier ? suppliers.find((s) => s.id === supplier)?.name : 'No seleccionado'}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-xs text-gray-600 font-medium">Monto:</span>
                <span className="text-xs font-semibold text-gray-900">
                  {amount ? formatCurrency(parseInt(amount)) : formatCurrency(0)}
                </span>
              </div>
            </div>

            <div className="my-3">
              <div className="h-px bg-gradient-to-r from-transparent via-gray-300 to-transparent"></div>
            </div>

            <div className="mb-3">
              <div className="flex justify-between items-center">
                <span className="text-sm font-semibold text-gray-800">Total a Pagar:</span>
                <div className="text-right">
                  <span className="text-lg font-bold bg-gradient-to-r from-green-500 to-emerald-500 bg-clip-text text-transparent">
                    {amount ? formatCurrency(parseInt(amount)) : formatCurrency(0)}
                  </span>
                  <p className="text-xs text-gray-500">Pesos colombianos</p>
                </div>
              </div>
            </div>

            <div>
              <Badge
                variant="secondary"
                className="w-full justify-center py-2 bg-gradient-to-r from-yellow-100 to-orange-100 text-yellow-700 border border-yellow-200 hover:from-yellow-200 hover:to-orange-200"
              >
                <DollarSign className="h-3 w-3 mr-2" />
                Sin comisiones adicionales
              </Badge>
            </div>
          </GradientCard>

          <Card className="mt-4 overflow-hidden border-0 shadow-lg !p-0">
            <div className="bg-gradient-to-r from-cyan-500 to-blue-500 text-white p-4 !m-0">
              <h4 className="font-semibold text-sm text-white flex items-center">
                <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fillRule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                    clipRule="evenodd"
                  />
                </svg>
                Beneficios MultiPagos
              </h4>
            </div>
            <CardContent className="p-4">
              <div className="space-y-3">
                <div className="flex items-start space-x-3">
                  <div className="w-2 h-2 rounded-full bg-green-500 mt-2 flex-shrink-0"></div>
                  <div>
                    <h5 className="font-medium text-sm text-green-700">Recargas Instantáneas</h5>
                    <p className="text-xs text-gray-600">Tu saldo se refleja al momento</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3">
                  <div className="w-2 h-2 rounded-full bg-blue-500 mt-2 flex-shrink-0"></div>
                  <div>
                    <h5 className="font-medium text-sm text-blue-700">Sin Comisiones</h5>
                    <p className="text-xs text-gray-600">0% de costo adicional</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3">
                  <div className="w-2 h-2 rounded-full bg-purple-500 mt-2 flex-shrink-0"></div>
                  <div>
                    <h5 className="font-medium text-sm text-purple-700">Disponible 24/7</h5>
                    <p className="text-xs text-gray-600">Recarga cuando quieras</p>
                  </div>
                </div>
                <div className="flex items-start space-x-3">
                  <div className="w-2 h-2 rounded-full bg-orange-500 mt-2 flex-shrink-0"></div>
                  <div>
                    <h5 className="font-medium text-sm text-orange-700">Soporte Premium</h5>
                    <p className="text-xs text-gray-600">Atención personalizada</p>
                  </div>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t">
                <div className="flex items-center justify-center space-x-2 text-green-600">
                  <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path
                      fillRule="evenodd"
                      d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                      clipRule="evenodd"
                    />
                  </svg>
                  <span className="text-xs font-medium">Transacciones 100% Seguras</span>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      <TopupTicketDialog isOpen={showTicketDialog} onClose={() => setShowTicketDialog(false)} ticketData={ticketData} />
    </div>
  );
}
