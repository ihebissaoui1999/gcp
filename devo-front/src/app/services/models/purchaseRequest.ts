export enum purchaseStatus {
  New = 'New',
  Resolved = 'Resolved',
  InProgress = 'InProgress',
  Completed = 'Completed'
}

export interface purchaseRequest {
     purchaseid?: number;
     purchasedate?: string;
    description?: string;
  purchaseStatus?: purchaseStatus;
}