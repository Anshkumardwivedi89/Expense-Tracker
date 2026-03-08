import { Component, OnInit } from '@angular/core';
import { ProfileService } from '../../services/profile.service';
import { UserProfile } from '../../models/user-profile';

@Component({
  selector: 'app-profile',
  template: `
    <div class="profile-container">
      <div class="header">
        <h2>My Profile</h2>
        <a routerLink="/" class="btn-back">Back to Dashboard</a>
      </div>

      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div *ngIf="successMessage" class="success-message">
        {{ successMessage }}
      </div>

      <div class="profile-card" *ngIf="profile">
        <div class="profile-avatar">
          <div class="avatar-placeholder" *ngIf="!profile.avatarUrl">
            <span>👤</span>
          </div>
          <img *ngIf="profile.avatarUrl" [src]="profile.avatarUrl" alt="User Avatar" class="avatar-image" (error)="onAvatarError()">
        </div>

        <div class="profile-form">
          <h3>Profile Settings</h3>
          
          <div class="form-group">
            <label>Avatar URL:
              <input [(ngModel)]="profile.avatarUrl" placeholder="Enter avatar image URL" (change)="onAvatarUrlChange()">
            </label>
          </div>

          <h3>Notification Settings</h3>
          
          <div class="notification-settings">
            <div class="setting-item" *ngFor="let setting of notificationKeys">
              <label>
                <input 
                  type="checkbox" 
                  [checked]="profile.notificationSettings?.[setting]"
                  (change)="toggleNotification(setting)">
                {{ formatSettingName(setting) }}
              </label>
            </div>
          </div>

          <button (click)="saveProfile()" [disabled]="saving" class="btn-save">
            {{ saving ? 'Saving...' : 'Save Changes' }}
          </button>
        </div>
      </div>

      <div *ngIf="!profile" class="loading">
        Loading profile...
      </div>
    </div>
  `,
  styles: [`
    .profile-container {
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

    .success-message {
      background-color: #d4edda;
      color: #155724;
      padding: 1rem;
      border-radius: 4px;
      margin-bottom: 1rem;
      border: 1px solid #c3e6cb;
    }

    .profile-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .profile-avatar {
      text-align: center;
      margin-bottom: 2rem;
    }

    .avatar-placeholder {
      width: 120px;
      height: 120px;
      margin: 0 auto;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 3rem;
    }

    .avatar-image {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      object-fit: cover;
      display: block;
      margin: 0 auto;
      border: 3px solid #667eea;
    }

    .profile-form h3 {
      color: #333;
      margin-top: 1.5rem;
      margin-bottom: 1rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    .form-group label {
      display: flex;
      flex-direction: column;
      color: #555;
      font-weight: 500;
    }

    .form-group input[type="text"] {
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      margin-top: 0.5rem;
      font-size: 1rem;
    }

    .form-group input:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
    }

    .notification-settings {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }

    .setting-item {
      padding: 1rem;
      background-color: #f9f9f9;
      border-radius: 4px;
      border: 1px solid #ddd;
    }

    .setting-item label {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin: 0;
      cursor: pointer;
    }

    .setting-item input[type="checkbox"] {
      width: 20px;
      height: 20px;
      cursor: pointer;
    }

    .btn-save {
      width: 100%;
      padding: 0.75rem;
      margin-top: 1.5rem;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .btn-save:hover:not(:disabled) {
      background-color: #45a049;
    }

    .btn-save:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }

    .loading {
      text-align: center;
      padding: 2rem;
      color: #666;
    }
  `]
})
export class ProfileComponent implements OnInit {
  profile: UserProfile | null = null;
  notificationKeys: string[] = [];
  errorMessage = '';
  successMessage = '';
  saving = false;

  constructor(private profileService: ProfileService) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.profileService.getProfile().subscribe(
      data => {
        this.profile = data;
        this.notificationKeys = Object.keys(data.notificationSettings || {});
        this.errorMessage = '';
      },
      error => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Failed to load profile';
      }
    );
  }

  toggleNotification(setting: string) {
    if (!this.profile?.notificationSettings) {
      this.profile = { ...this.profile, notificationSettings: {} };
    }
    this.profile.notificationSettings![setting] = !this.profile.notificationSettings![setting];
  }

  formatSettingName(setting: string): string {
    return setting
      .replace(/([A-Z])/g, ' $1')
      .trim()
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  }

  saveProfile() {
    if (!this.profile) return;

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.profileService.updateProfile(this.profile).subscribe(
      (updatedProfile) => {
        this.saving = false;
        this.profile = updatedProfile;
        this.successMessage = 'Profile updated successfully!';
        this.profileService.notifyProfileUpdate(updatedProfile);
        setTimeout(() => this.successMessage = '', 3000);
      },
      error => {
        this.saving = false;
        console.error('Error saving profile:', error);
        this.errorMessage = 'Failed to save profile. Please try again.';
      }
    );
  }

  onAvatarUrlChange() {
    if (!this.profile?.avatarUrl) return;
    // Avatar will be displayed via image binding when valid
  }

  onAvatarError() {
    // If image fails to load, show placeholder
    if (this.profile) {
      this.profile.avatarUrl = '';
    }
  }
}
