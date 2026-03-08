import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification';

@Component({
  selector: 'app-notifications',
  template: `
    <div class="notifications-container">
      <div class="header">
        <h2>Notifications</h2>
        <a routerLink="/" class="btn-back">Back to Dashboard</a>
      </div>

      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div class="notifications-card">
        <div *ngIf="notifications.length === 0" class="no-notifications">
          <p>📭 You have no notifications</p>
        </div>

        <div *ngIf="notifications.length > 0" class="notifications-list">
          <div *ngFor="let notif of notifications" class="notification-item" [ngClass]="notif.type.toLowerCase()">
            <div class="notification-type">{{ notif.type }}</div>
            <div class="notification-message">{{ notif.message }}</div>
            <div class="notification-date">{{ formatDate(notif.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notifications-container {
      padding: 2rem;
      max-width: 800px;
      margin: 0 auto;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    h2 {
      color: #333;
      margin: 0;
    }

    .btn-back {
      padding: 0.5rem 1rem;
      background-color: #007bff;
      color: white;
      text-decoration: none;
      border-radius: 4px;
      transition: background-color 0.3s;
    }

    .btn-back:hover {
      background-color: #0056b3;
    }

    .error-message {
      background-color: #f8d7da;
      color: #721c24;
      padding: 1rem;
      border-radius: 4px;
      margin-bottom: 1rem;
      border: 1px solid #f5c6cb;
    }

    .notifications-card {
      background: white;
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .no-notifications {
      text-align: center;
      padding: 2rem;
      color: #999;
    }

    .notifications-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .notification-item {
      padding: 1rem;
      border-left: 4px solid #ddd;
      background-color: #f9f9f9;
      border-radius: 4px;
    }

    .notification-item.success {
      border-left-color: #28a745;
      background-color: #d4edda;
    }

    .notification-item.info {
      border-left-color: #17a2b8;
      background-color: #d1ecf1;
    }

    .notification-item.warning {
      border-left-color: #ffc107;
      background-color: #fff3cd;
    }

    .notification-item.error {
      border-left-color: #dc3545;
      background-color: #f8d7da;
    }

    .notification-type {
      font-size: 0.85rem;
      font-weight: bold;
      color: #555;
      text-transform: uppercase;
      margin-bottom: 0.5rem;
    }

    .notification-message {
      color: #333;
      margin-bottom: 0.5rem;
    }

    .notification-date {
      font-size: 0.85rem;
      color: #999;
    }
  `]
})
export class NotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  errorMessage = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    this.notificationService.getNotifications().subscribe(
      data => {
        this.notifications = data.sort((a, b) => {
          const dateA = new Date(a.createdAt || 0).getTime();
          const dateB = new Date(b.createdAt || 0).getTime();
          return dateB - dateA;
        });
        this.errorMessage = '';
      },
      error => {
        console.error('Error loading notifications:', error);
        this.errorMessage = 'Failed to load notifications';
      }
    );
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'Just now';
    const d = new Date(date);
    return d.toLocaleDateString() + ' ' + d.toLocaleTimeString();
  }
}
