export interface UserProfile {
  userId?: string;
  email?: string;
  phoneNumber?: string;
  avatarUrl?: string;
  preferences?: {
    [key: string]: any;
  };
  notificationSettings?: {
    [key: string]: boolean;
  };
}
