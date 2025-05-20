export enum CategoryType {
  Technical = 'Technical',
  Rh = 'Rh',
  Finance = 'Finance',
  Employee = 'Employee'
}

export enum StatusType {
  New = 'New',
  Resolved = 'Resolved',
  InProgress = 'InProgress',
  Completed = 'Completed'
}

export enum PriorityType {
  Low = 'Low',
  Medium = 'Medium',
  High = 'High'
}

export interface Ticket {
  ticketid?: number;
  title?: string;
  description?: string;
  creationdate?: string;
  lastmodifier?: string;
  categoryType?: CategoryType;
  statusType?: StatusType;
  priorityType?: PriorityType;
  accepted? :boolean;
    createdBy?: string;
  
}

