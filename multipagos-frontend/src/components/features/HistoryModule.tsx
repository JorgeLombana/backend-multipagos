import { Card, CardContent } from '../ui/card';
import { Badge } from '../ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../ui/table';
import { Button } from '../ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { StatsCard } from '../ui/gradient-card';
import {
  History,
  Calendar,
  Smartphone,
  DollarSign,
  CheckCircle,
  XCircle,
  Loader2,
  ChevronLeft,
  ChevronRight,
  ChevronsLeft,
  ChevronsRight,
} from 'lucide-react';
import { useState, useEffect } from 'react';
import { topupService } from '@/services/topup';
import { toast } from 'sonner';
import {
  GRADIENTS,
  PAGINATION,
  formatTransactionDate,
  generatePaginationNumbers,
  extractErrorMessage,
  filterCompletedTransactions,
} from '@/lib';

interface Transaction {
  id: string;
  cellPhone: string;
  value: number;
  supplierName: string;
  status: string;
  transactionalID: string | null;
  createdAt: string;
  message: string;
}

export function HistoryModule() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(PAGINATION.DEFAULT_PAGE_SIZE);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [hasPrevious, setHasPrevious] = useState(false);

  useEffect(() => {
    loadTransactionHistory();
  }, [page, pageSize]);

  useEffect(() => {
    const handleRefresh = () => {
      setPage(0);
      loadTransactionHistory();
    };

    window.addEventListener('historyRefresh', handleRefresh);
    return () => window.removeEventListener('historyRefresh', handleRefresh);
  }, []);

  const loadTransactionHistory = async () => {
    try {
      setIsLoading(true);
      const response = await topupService.getTransactionHistory({
        page,
        size: pageSize,
        sortField: 'createdAt',
        sortDirection: 'DESC',
      });

      if (response.status === 'success' && response.data) {
        const pagedData = response.data as any;
        setTransactions(pagedData.content || []);
        setTotalElements(pagedData.totalElements || 0);
        setTotalPages(pagedData.totalPages || 0);
        setHasNext(pagedData.hasNext || false);
        setHasPrevious(pagedData.hasPrevious || false);
      }
    } catch (error: any) {
      const message = extractErrorMessage(error);
      toast.error(`Error al cargar el historial: ${message}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNextPage = () => {
    if (hasNext) {
      setPage(page + 1);
    }
  };

  const handlePrevPage = () => {
    if (hasPrevious) {
      setPage(page - 1);
    }
  };

  const handleFirstPage = () => {
    setPage(0);
  };

  const handleLastPage = () => {
    setPage(totalPages - 1);
  };

  const handlePageSizeChange = (newSize: string) => {
    const size = parseInt(newSize) as typeof PAGINATION.DEFAULT_PAGE_SIZE;
    setPageSize(size);
    setPage(0);
  };

  const generatePageNumbers = () => {
    return generatePaginationNumbers(page, totalPages, PAGINATION.MAX_VISIBLE_PAGES);
  };

  const getStatusIcon = (status: string) => {
    switch (status.toUpperCase()) {
      case 'SUCCESS':
      case 'COMPLETED':
        return <CheckCircle className="h-4 w-4 text-green-500" />;
      case 'FAILED':
        return <XCircle className="h-4 w-4 text-red-500" />;
      default:
        return <History className="h-4 w-4 text-yellow-500" />;
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status.toUpperCase()) {
      case 'SUCCESS':
      case 'COMPLETED':
        return (
          <Badge variant="default" className="bg-green-100 text-green-800 border-green-200">
            Completada
          </Badge>
        );
      case 'FAILED':
        return <Badge variant="destructive">Fallida</Badge>;
      default:
        return <Badge variant="secondary">Pendiente</Badge>;
    }
  };

  const formatDate = (dateString: string) => {
    return formatTransactionDate(dateString);
  };

  const totalTransactions = transactions.length;
  const completedTransactions = filterCompletedTransactions(transactions).length;

  return (
    <div className="space-y-6">
      <div className="grid gap-4 md:grid-cols-3">
        <StatsCard
          title="Total Transacciones"
          value={totalTransactions}
          description="Transacciones registradas"
          icon={History}
          gradient={GRADIENTS.info}
        />

        <StatsCard
          title="Exitosas"
          value={completedTransactions}
          description="Transacciones completadas"
          icon={CheckCircle}
          gradient={GRADIENTS.success}
          valueColor="text-green-600"
        />

        <StatsCard
          title="Monto Total"
          value="---"
          description="Disponible próximamente"
          icon={DollarSign}
          gradient={GRADIENTS.warning}
          valueColor="text-gray-400"
          isPlaceholder={true}
        />
      </div>
      <Card className="shadow-lg overflow-hidden !p-0 border border-slate-200">
        <div className={`${GRADIENTS.primary} text-white p-4 !m-0`}>
          <h4 className="flex items-center text-white font-semibold text-sm">
            <Calendar className="h-4 w-4 mr-2" />
            Transacciones Recientes
          </h4>
          <p className="text-blue-100 text-xs mt-1">Listado detallado de todas tus transacciones</p>
        </div>
        <CardContent className="p-4">
          {isLoading ? (
            <div className="flex items-center justify-center h-32">
              <Loader2 className="h-8 w-8 animate-spin text-slate-500" />
              <span className="ml-2 text-slate-600">Cargando historial...</span>
            </div>
          ) : transactions.length > 0 ? (
            <>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Fecha & Hora</TableHead>
                    <TableHead>Tipo</TableHead>
                    <TableHead>Teléfono</TableHead>
                    <TableHead>Operador</TableHead>
                    <TableHead>Monto</TableHead>
                    <TableHead>Estado</TableHead>
                    <TableHead>ID Transacción</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {transactions.map((transaction) => {
                    const { date, time } = formatDate(transaction.createdAt);
                    return (
                      <TableRow key={transaction.id}>
                        <TableCell className="font-mono text-sm">{transaction.id.slice(0, 8)}...</TableCell>
                        <TableCell>
                          <div className="flex flex-col">
                            <span className="text-sm text-slate-800">{date}</span>
                            <span className="text-xs text-slate-500">{time}</span>
                          </div>
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center">
                            <Smartphone className="h-4 w-4 mr-2 text-blue-600" />
                            <span className="text-slate-700">Recarga Móvil</span>
                          </div>
                        </TableCell>
                        <TableCell className="font-mono text-slate-700">{transaction.cellPhone}</TableCell>
                        <TableCell>
                          <Badge variant="outline" className="border-slate-200 text-slate-700">
                            {transaction.supplierName}
                          </Badge>
                        </TableCell>
                        <TableCell className="font-semibold text-slate-800">
                          ${transaction.value.toLocaleString()}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center space-x-2">
                            {getStatusIcon(transaction.status)}
                            {getStatusBadge(transaction.status)}
                          </div>
                        </TableCell>
                        <TableCell className="font-mono text-xs">{transaction.transactionalID || 'N/A'}</TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>

              {totalElements > 0 && (
                <div className="flex flex-col sm:flex-row items-center justify-between gap-4 mt-6 pt-4 border-t">
                  <div className="flex flex-col sm:flex-row items-center gap-4">
                    <p className="text-sm text-slate-600">
                      Mostrando {Math.min(page * pageSize + 1, totalElements)} a{' '}
                      {Math.min((page + 1) * pageSize, totalElements)} de {totalElements} transacciones
                    </p>

                    <div className="flex items-center space-x-2">
                      <span className="text-sm text-slate-600">Mostrar:</span>
                      <Select value={pageSize.toString()} onValueChange={handlePageSizeChange}>
                        <SelectTrigger className="w-20">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="2">2</SelectItem>
                          <SelectItem value="5">5</SelectItem>
                          <SelectItem value="10">10</SelectItem>
                          <SelectItem value="20">20</SelectItem>
                          <SelectItem value="50">50</SelectItem>
                        </SelectContent>
                      </Select>
                      <span className="text-sm text-slate-600">por página</span>
                    </div>
                  </div>

                  <div className="flex items-center space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={handleFirstPage}
                      disabled={!hasPrevious || isLoading}
                      className="hidden sm:flex"
                    >
                      <ChevronsLeft className="h-4 w-4" />
                    </Button>

                    <Button variant="outline" size="sm" onClick={handlePrevPage} disabled={!hasPrevious || isLoading}>
                      <ChevronLeft className="h-4 w-4" />
                      <span className="hidden sm:inline ml-1">Anterior</span>
                    </Button>

                    <div className="hidden sm:flex items-center space-x-1">
                      {generatePageNumbers().map((pageNum) => (
                        <Button
                          key={pageNum}
                          variant={pageNum === page ? 'default' : 'outline'}
                          size="sm"
                          onClick={() => setPage(pageNum)}
                          disabled={isLoading}
                          className={`min-w-[2.5rem] ${
                            pageNum === page
                              ? `${GRADIENTS.primary} text-white ${GRADIENTS.primaryHover}`
                              : 'hover:bg-slate-50 border-slate-200'
                          }`}
                        >
                          {pageNum + 1}
                        </Button>
                      ))}
                    </div>

                    <div className="sm:hidden flex items-center space-x-2">
                      <span className="text-sm font-medium">
                        {page + 1} / {totalPages}
                      </span>
                    </div>

                    <Button variant="outline" size="sm" onClick={handleNextPage} disabled={!hasNext || isLoading}>
                      <span className="hidden sm:inline mr-1">Siguiente</span>
                      <ChevronRight className="h-4 w-4" />
                    </Button>

                    <Button
                      variant="outline"
                      size="sm"
                      onClick={handleLastPage}
                      disabled={!hasNext || isLoading}
                      className="hidden sm:flex"
                    >
                      <ChevronsRight className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className="flex items-center justify-center h-32 text-slate-600">
              <div className="text-center">
                <History className="h-12 w-12 mx-auto mb-2 opacity-50" />
                <p>No hay transacciones registradas</p>
                <p className="text-sm">¡Realiza tu primera recarga móvil!</p>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
