export interface Notification {
  id?: string;
  userId?: string;
  message: string;
  type: string;
  sent?: boolean;
  createdAt?: string;
}
