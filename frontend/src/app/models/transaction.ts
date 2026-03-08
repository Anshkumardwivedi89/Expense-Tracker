import { Category } from './category';

export enum TransactionType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

export interface Transaction {
  id?: number;
  type: TransactionType;
  category: Category;
  amount: number;
  date: string;
  description: string;
  budgetId?: string;
}
