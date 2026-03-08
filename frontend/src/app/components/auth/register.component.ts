import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.username.trim() || !this.email.trim() || !this.password.trim()) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }

    if (!this.email.includes('@')) {
      this.errorMessage = 'Please enter a valid email';
      return;
    }

    if (this.password.length < 4) {
      this.errorMessage = 'Password must be at least 4 characters';
      return;
    }

    this.loading = true;

    this.auth.register(this.username, this.email, this.password).subscribe(
      (response: string) => {
        this.loading = false;
        this.successMessage = 'Registration successful! Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      (error: any) => {
        this.loading = false;
        console.error('Registration error:', error);
        
        if (error.status === 409) {
          this.errorMessage = 'Username or email already exists';
        } else if (error.status === 0) {
          this.errorMessage = 'Cannot connect to server. Make sure the backend is running.';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (error.error) {
          this.errorMessage = error.error;
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
        }
      }
    );
  }
}