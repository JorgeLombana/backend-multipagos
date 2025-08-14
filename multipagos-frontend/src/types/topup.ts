export interface Supplier {
  id: string;
  name: string;
}

export interface TopupRequest {
  cellPhone: string;
  value: number;
  supplierId: string;
}

export interface TopUpTransactionResponse {
  id: string;
  cellPhone: string;
  value: number;
  supplierName: string;
  status: 'PENDING' | 'SUCCESS' | 'FAILED' | 'CANCELLED';
  transactionalID: string;
  createdAt: string;
  updatedAt: string;
  message: string;
}

export interface TopupResponse {
  status: string;
  message: string;
  data: TopUpTransactionResponse;
  timestamp: string;
  apiVersion: string;
}

export interface PageableInfo {
  pageNumber: number;
  pageSize: number;
  offset: number;
  paged: boolean;
  unpaged: boolean;
  sort: SortInfo;
}

export interface SortInfo {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface PagedResponse<T> {
  content: T[];
  pageable: PageableInfo;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface TransactionHistoryResponse {
  status: string;
  message: string;
  data: PagedResponse<TopUpTransactionResponse>;
  timestamp: string;
  apiVersion: string;
}

export interface SuppliersResponse {
  status: string;
  message: string;
  data: Supplier[];
  timestamp: string;
  apiVersion: string;
}
