import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { CheckCircle, Smartphone, Calendar, Receipt, Building2, Hash, DollarSign, Copy, Download } from 'lucide-react';
import { toast } from 'sonner';
import { formatCurrency, formatTransactionDate, formatPhoneNumber } from '../../lib';
import { GRADIENTS } from '../../lib';

interface TopupTicketData {
  id: string;
  cellPhone: string;
  value: number;
  supplierName: string;
  status: string;
  transactionalID: string;
  createdAt: string;
  updatedAt: string;
  message: string;
}

interface TopupTicketDialogProps {
  isOpen: boolean;
  onClose: () => void;
  ticketData: TopupTicketData | null;
}

export function TopupTicketDialog({ isOpen, onClose, ticketData }: TopupTicketDialogProps) {
  if (!ticketData) return null;

  const { date, time } = formatTransactionDate(ticketData.createdAt);

  const copyToClipboard = (text: string, label: string) => {
    navigator.clipboard.writeText(text);
    toast.success(`${label} copiado al portapapeles`);
  };

  const downloadTicket = () => {
    const ticketContent = `
MULTIPAGOS - COMPROBANTE DE RECARGA
=====================================

Fecha: ${date}
Hora: ${time}
ID Transacción: ${ticketData.transactionalID}

DETALLES DE LA RECARGA
-------------------------------------
Teléfono: ${formatPhoneNumber(ticketData.cellPhone)}
Operador: ${ticketData.supplierName}
Monto: ${formatCurrency(ticketData.value)}
Estado: ${ticketData.status}

ID Interno: ${ticketData.id}
Mensaje: ${ticketData.message}

=====================================
MultiPagos - Portal Transaccional
    `;

    const blob = new Blob([ticketContent], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `ticket-recarga-${ticketData.transactionalID}.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    toast.success('Ticket descargado exitosamente');
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-md mx-auto max-h-[90vh] overflow-y-auto bg-white">
        <DialogHeader className="text-center space-y-4 pb-6">
          {/* Icon and Title Section */}
          <div className="space-y-3">
            <div className="flex justify-center"></div>

            <div className="space-y-2 text-center">
              <DialogTitle className="text-2xl font-bold text-green-700">¡Recarga Exitosa!</DialogTitle>
              <p className="text-muted-foreground">Tu recarga ha sido procesada correctamente</p>
            </div>
          </div>

          {/* Status Badge */}
          <div className="flex justify-center">
            <Badge className="bg-green-100 text-green-800 border-green-200 px-6 py-2 text-sm font-medium shadow-sm">
              <CheckCircle className="h-4 w-4 mr-2" />
              {ticketData.status === 'COMPLETED' ? 'Completada' : ticketData.status}
            </Badge>
          </div>
        </DialogHeader>

        <div className="space-y-6">
          {/* Ticket Card */}
          <Card className="border-2 border-dashed border-gray-300 bg-gradient-to-br from-gray-50 to-gray-100">
            <CardContent className="p-4 space-y-4">
              {/* Header del Ticket */}
              <div className="text-center border-b pb-3">
                <h3 className="font-bold text-lg">COMPROBANTE DE RECARGA</h3>
                <p className="text-xs text-muted-foreground">MultiPagos - Portal Transaccional</p>
              </div>

              {/* Detalles Principales */}
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Smartphone className="h-4 w-4 text-blue-500" />
                    <span className="text-sm font-medium">Teléfono:</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <span className="font-mono text-sm">{formatPhoneNumber(ticketData.cellPhone)}</span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-6 w-6 p-0"
                      onClick={() => copyToClipboard(ticketData.cellPhone, 'Teléfono')}
                    >
                      <Copy className="h-3 w-3" />
                    </Button>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Building2 className="h-4 w-4 text-purple-500" />
                    <span className="text-sm font-medium">Operador:</span>
                  </div>
                  <span className="font-semibold text-sm">{ticketData.supplierName}</span>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <DollarSign className="h-4 w-4 text-green-500" />
                    <span className="text-sm font-medium">Monto:</span>
                  </div>
                  <span className="font-bold text-lg text-green-600">{formatCurrency(ticketData.value)}</span>
                </div>

                <Separator />

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Calendar className="h-4 w-4 text-orange-500" />
                    <span className="text-sm font-medium">Fecha:</span>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-medium">{date}</p>
                    <p className="text-xs text-muted-foreground">{time}</p>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Receipt className="h-4 w-4 text-indigo-500" />
                    <span className="text-sm font-medium">ID Transacción:</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <span className="font-mono text-xs">{ticketData.transactionalID}</span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-6 w-6 p-0"
                      onClick={() => copyToClipboard(ticketData.transactionalID, 'ID de transacción')}
                    >
                      <Copy className="h-3 w-3" />
                    </Button>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Hash className="h-4 w-4 text-gray-500" />
                    <span className="text-sm font-medium">ID Interno:</span>
                  </div>
                  <span className="font-mono text-sm">{ticketData.id}</span>
                </div>
              </div>

              {/* Mensaje */}
              <div className="bg-green-50 border border-green-200 rounded-lg p-3">
                <div className="flex items-center space-x-2">
                  <CheckCircle className="h-4 w-4 text-green-500 flex-shrink-0" />
                  <p className="text-sm text-green-700">{ticketData.message}</p>
                </div>
              </div>

              {/* Footer del Ticket */}
              <div className="text-center text-xs text-muted-foreground border-t pt-3">
                <p>Conserve este comprobante para sus registros</p>
                <p>MultiPagos • Recargas Móviles Seguras</p>
              </div>
            </CardContent>
          </Card>

          {/* Action Buttons */}
          <div className="grid grid-cols-2 gap-4 pt-2">
            <Button
              variant="outline"
              onClick={downloadTicket}
              className="flex items-center justify-center space-x-2 h-11"
            >
              <Download className="h-4 w-4" />
              <span>Descargar</span>
            </Button>
            <Button onClick={onClose} className={`${GRADIENTS.primary} hover:from-blue-600 hover:to-purple-700 h-11`}>
              <span>Continuar</span>
            </Button>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
