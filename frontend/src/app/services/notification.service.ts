import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Notification } from '../models/notification';

export interface SMSRequest {
  phoneNumber: string;
  message: string;
}

export interface EmailRequest {
  email: string;
  subject: string;
  body: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private http: HttpClient) {}

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${environment.apiUrl}/notifications`);
  }

  createNotification(message: string, type: string): Observable<Notification> {
    return this.http.post<Notification>(`${environment.apiUrl}/notifications/create`, {}, {
      params: { message, type }
    });
  }

  sendSMS(phoneNumber: string, message: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/notifications/send-sms`, { phoneNumber, message });
  }

  sendEmail(email: string, subject: string, body: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/notifications/send-email`, { email, subject, body });
  }
}
