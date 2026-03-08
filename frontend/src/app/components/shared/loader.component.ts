import { Component, Input, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="isLoading" class="loader-overlay" [ngClass]="status">
      <div class="spinner-container">
        <div *ngIf="status === 'loading'" class="spinner"></div>
        <div *ngIf="status === 'success'" class="success-icon">✓</div>
        <div *ngIf="status === 'error'" class="error-icon">✕</div>
        <p class="loader-message">{{ message }}</p>
      </div>
    </div>
  `,
  styles: [`
    .loader-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 9999;
      transition: opacity 0.3s ease;
    }

    .loader-overlay.success {
      background-color: rgba(0, 0, 0, 0.3);
    }

    .loader-overlay.error {
      background-color: rgba(0, 0, 0, 0.3);
    }

    .spinner-container {
      text-align: center;
      background: white;
      padding: 40px;
      border-radius: 10px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      animation: slideIn 0.3s ease;
    }

    @keyframes slideIn {
      from {
        transform: translateY(-20px);
        opacity: 0;
      }
      to {
        transform: translateY(0);
        opacity: 1;
      }
    }

    .spinner {
      border: 5px solid #f3f3f3;
      border-top: 5px solid #3498db;
      border-radius: 50%;
      width: 50px;
      height: 50px;
      margin: 0 auto 20px;
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    .success-icon {
      width: 60px;
      height: 60px;
      margin: 0 auto 15px;
      background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 36px;
      color: white;
      font-weight: bold;
      animation: scaleIn 0.4s ease;
    }

    @keyframes scaleIn {
      0% {
        transform: scale(0);
      }
      50% {
        transform: scale(1.1);
      }
      100% {
        transform: scale(1);
      }
    }

    .error-icon {
      width: 60px;
      height: 60px;
      margin: 0 auto 15px;
      background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 36px;
      color: white;
      font-weight: bold;
      animation: shake 0.5s ease;
    }

    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25% { transform: translateX(-5px); }
      75% { transform: translateX(5px); }
    }

    .loader-message {
      color: #333;
      font-size: 16px;
      font-weight: 500;
      margin: 0;
      white-space: nowrap;
    }

    .loader-overlay.success .loader-message {
      color: #27ae60;
      font-weight: 600;
    }

    .loader-overlay.error .loader-message {
      color: #e74c3c;
      font-weight: 600;
    }

    @media (max-width: 768px) {
      .spinner-container {
        padding: 30px;
      }

      .spinner {
        width: 40px;
        height: 40px;
      }

      .loader-message {
        font-size: 14px;
      }
    }
  `]
})
export class LoaderComponent implements OnDestroy {
  @Input() isLoading = false;
  @Input() message = 'Loading...';
  
  status: 'loading' | 'success' | 'error' = 'loading';
  private hideTimeout: any;

  ngOnDestroy() {
    if (this.hideTimeout) {
      clearTimeout(this.hideTimeout);
    }
  }

  showSuccess(message: string, duration: number = 2000) {
    this.isLoading = true;
    this.status = 'success';
    this.message = message;

    if (this.hideTimeout) {
      clearTimeout(this.hideTimeout);
    }

    this.hideTimeout = setTimeout(() => {
      this.isLoading = false;
    }, duration);
  }

  showError(message: string, duration: number = 2500) {
    this.isLoading = true;
    this.status = 'error';
    this.message = message;

    if (this.hideTimeout) {
      clearTimeout(this.hideTimeout);
    }

    this.hideTimeout = setTimeout(() => {
      this.isLoading = false;
    }, duration);
  }

  showLoading(message: string) {
    if (this.hideTimeout) {
      clearTimeout(this.hideTimeout);
    }
    this.isLoading = true;
    this.status = 'loading';
    this.message = message;
  }

  hide() {
    this.isLoading = false;
    if (this.hideTimeout) {
      clearTimeout(this.hideTimeout);
    }
  }
}

