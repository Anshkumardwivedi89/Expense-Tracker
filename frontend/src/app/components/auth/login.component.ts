import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.username.trim() || !this.password.trim()) {
      this.errorMessage = 'Please enter both username and password';
      return;
    }

    this.loading = true;

    this.auth.login(this.username, this.password).subscribe(
      (token: string) => {
        this.loading = false;
        this.auth.setToken(token);
        this.successMessage = 'Login successful! Redirecting...';
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 1000);
      },
      (error: any) => {
        this.loading = false;
        console.error('Login error:', error);
        
        if (error.status === 401 || error.status === 403) {
          this.errorMessage = 'Invalid username or password';
        } else if (error.status === 0) {
          this.errorMessage = 'Cannot connect to server. Make sure the backend is running.';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Login failed. Please try again.';
        }
      }
    );
  }
}