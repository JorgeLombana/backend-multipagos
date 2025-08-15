import { apiService } from './api';
import { BUSINESS_RULES, VALID_SUPPLIER_IDS, CACHE_CONFIG } from '@/lib/constants';
import type {
  TopupRequest,
  TopupResponse,
  SuppliersResponse,
  TransactionHistoryResponse,
  PaginationParams
} from '@/types';

/**
 * Topup service for mobile recharge operations
 */
class TopupService {
  private suppliersCache: { data: any[]; timestamp: number } | null = null;
  private readonly CACHE_DURATION = CACHE_CONFIG.SUPPLIERS_DURATION;

  /**
   * Get all available suppliers with caching
   * @returns Promise with suppliers response
   */
  async getSuppliers(): Promise<SuppliersResponse> {
    try {
      // Check cache first
      if (this.isSuppliersDataFresh()) {
        return {
          status: 'success',
          message: 'Proveedores obtenidos desde caché',
          data: this.suppliersCache!.data,
          timestamp: new Date().toISOString(),
          apiVersion: 'v1'
        };
      }

      const response = await apiService.get<any>('/suppliers');
      
      // Cache the response
      if (response.status === 'success' && response.data) {
        this.suppliersCache = {
          data: response.data,
          timestamp: Date.now()
        };
      }
      
      return {
        status: response.status,
        message: response.message,
        data: response.data || [],
        timestamp: response.timestamp,
        apiVersion: response.apiVersion
      };
    } catch (error) {
      throw error;
    }
  }

  /**
   * Process a mobile topup/recharge
   * @param data - Topup request data
   * @returns Promise with topup response
   */
  async processTopup(data: TopupRequest): Promise<TopupResponse> {
    try {
      // Client-side validation before API call
      this.validateTopupRequest(data);

      const response = await apiService.post<any>('/topup', data);
      
      return {
        status: response.status,
        message: response.message,
        data: response.data,
        timestamp: response.timestamp,
        apiVersion: response.apiVersion
      };
    } catch (error) {
      throw error;
    }
  }

  /**
   * Get user's transaction history with pagination
   * @param params - Pagination and sorting parameters
   * @returns Promise with transaction history response
   */
  async getTransactionHistory(params: PaginationParams = {}): Promise<TransactionHistoryResponse> {
    try {
      const queryParams = new URLSearchParams();
      
      // Set default values and validate params
      const validatedParams = this.validatePaginationParams(params);
      
      Object.entries(validatedParams).forEach(([key, value]) => {
        if (value !== undefined) {
          queryParams.append(key, value.toString());
        }
      });

      const url = `/topup/history${queryParams.toString() ? `?${queryParams.toString()}` : ''}`;
      const response = await apiService.get<any>(url);
      
      return {
        status: response.status,
        message: response.message,
        data: response.data,
        timestamp: response.timestamp,
        apiVersion: response.apiVersion
      };
    } catch (error) {
      throw error;
    }
  }

  /**
   * Clear suppliers cache
   */
  clearSuppliersCache(): void {
    this.suppliersCache = null;
  }

  /**
   * Check if suppliers cache is still fresh
   */
  private isSuppliersDataFresh(): boolean {
    if (!this.suppliersCache) return false;
    
    const now = Date.now();
    return (now - this.suppliersCache.timestamp) < this.CACHE_DURATION;
  }

  /**
   * Validate topup request data
   * @param data - Topup request to validate
   */
  private validateTopupRequest(data: TopupRequest): void {
    if (!data.cellPhone || typeof data.cellPhone !== 'string') {
      throw new Error('Número de celular es requerido');
    }

    if (!BUSINESS_RULES.TOPUP.PHONE_PATTERN.test(data.cellPhone)) {
      throw new Error('El número de celular debe empezar con 3 y tener exactamente 10 dígitos');
    }

    if (!data.value || typeof data.value !== 'number') {
      throw new Error('El valor es requerido');
    }

    if (data.value < BUSINESS_RULES.TOPUP.MIN_VALUE || data.value > BUSINESS_RULES.TOPUP.MAX_VALUE) {
      throw new Error(`El valor debe estar entre $${BUSINESS_RULES.TOPUP.MIN_VALUE.toLocaleString()} y $${BUSINESS_RULES.TOPUP.MAX_VALUE.toLocaleString()}`);
    }

    if (!data.supplierId || typeof data.supplierId !== 'string') {
      throw new Error('El proveedor es requerido');
    }

    if (!VALID_SUPPLIER_IDS.includes(data.supplierId)) {
      throw new Error('Proveedor no válido');
    }
  }

  /**
   * Validate and set defaults for pagination parameters
   * @param params - Raw pagination parameters
   * @returns Validated pagination parameters
   */
  private validatePaginationParams(params: PaginationParams): Required<PaginationParams> {
    const defaults: Required<PaginationParams> = {
      page: BUSINESS_RULES.PAGINATION.DEFAULT_PAGE,
      size: BUSINESS_RULES.PAGINATION.DEFAULT_SIZE,
      sortField: BUSINESS_RULES.PAGINATION.DEFAULT_SORT_FIELD,
      sortDirection: BUSINESS_RULES.PAGINATION.DEFAULT_SORT_DIRECTION
    };

    return {
      page: Math.max(0, params.page ?? defaults.page),
      size: Math.min(BUSINESS_RULES.PAGINATION.MAX_SIZE, Math.max(1, params.size ?? defaults.size)),
      sortField: params.sortField ?? defaults.sortField,
      sortDirection: params.sortDirection ?? defaults.sortDirection
    };
  }
}

export const topupService = new TopupService();
