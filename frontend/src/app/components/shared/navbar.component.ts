import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '../../services/notification.service';
import { Router } from '@angular/router';
import { Subject, interval } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  unreadCount = 0;
  private destroy$ = new Subject<void>();

  constructor(
    public auth: AuthService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    if (this.auth.loggedIn()) {
      this.loadNotifications();
      // Poll for new notifications every 10 seconds
      interval(10000)
        .pipe(
          switchMap(() => this.notificationService.getNotifications()),
          takeUntil(this.destroy$)
        )
        .subscribe(
          notifications => {
            this.unreadCount = notifications.filter(n => !n.sent).length;
          },
          error => console.error('Error loading notifications:', error)
        );
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadNotifications() {
    this.notificationService.getNotifications().subscribe(
      notifications => {
        this.unreadCount = notifications.filter(n => !n.sent).length;
      },
      error => console.error('Error loading notifications:', error)
    );
  }

  logout() {
    if (confirm('Are you sure you want to logout?')) {
      this.auth.logout();
      this.router.navigate(['/login']);
    }
  }
}