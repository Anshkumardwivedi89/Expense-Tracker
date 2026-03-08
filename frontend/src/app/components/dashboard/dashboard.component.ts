import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TransactionService } from '../../services/transaction.service';
import { DashboardService, DashboardSummary } from '../../services/dashboard.service';
import { CategoryService } from '../../services/category.service';
import { NotificationService } from '../../services/notification.service';
import { ProfileService } from '../../services/profile.service';
import { LoaderComponent } from '../shared/loader.component';
import { Transaction, TransactionType } from '../../models/transaction';
import { Category, CategoryType } from '../../models/category';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LoaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  @ViewChild('loaderComponent') loaderComponent!: LoaderComponent;
  
  recent: Transaction[] = [];
  summary: DashboardSummary | null = null;
  categories: Category[] = [];
  newTx: Partial<Transaction> = { type: TransactionType.EXPENSE };
  transactionType = TransactionType;
  addingTransaction = false;
  deletingTransactionId: number | undefined;
  errorMessage = '';
  successMessage = '';
  sendSMSNotification = false;
  userPhone: string | null = null;
  userEmail: string | null = null;
  isLoading = false;
  loaderMessage = '';

  constructor(
    private tx: TransactionService,
    private dashboard: DashboardService,
    private categoryService: CategoryService,
    private notificationService: NotificationService,
    private profileService: ProfileService
  ) {}

  get filteredCategories(): Category[] {
    return this.categories.filter(c => String(c.type) === String(this.newTx.type));
  }

  ngOnInit() {
    this.loadRecent();
    this.loadSummary();
    this.loadCategories();
    this.loadUserProfile();
  }

  loadUserProfile() {
    this.profileService.getProfile().subscribe(
      profile => {
        this.userPhone = profile.phoneNumber || null;
        this.userEmail = profile.email || null;
      },
      error => {
        console.error('Error loading user profile:', error);
      }
    );
  }

  loadRecent() {
    this.tx.recent().subscribe(
      data => this.recent = data,
      error => {
        console.error('Error loading recent transactions:', error);
        this.errorMessage = 'Failed to load recent transactions';
      }
    );
  }

  loadSummary() {
    this.dashboard.getSummary().subscribe(
      data => this.summary = data,
      error => {
        console.error('Error loading summary:', error);
        this.errorMessage = 'Failed to load summary data';
      }
    );
  }

  loadCategories() {
    // Load both INCOME and EXPENSE categories
    this.categoryService.get(CategoryType.INCOME).subscribe(
      incomeData => {
        this.categoryService.get(CategoryType.EXPENSE).subscribe(
          expenseData => {
            this.categories = [...incomeData, ...expenseData];
            this.errorMessage = '';
          },
          error => {
            console.error('Error loading expense categories:', error);
            this.errorMessage = 'Failed to load categories';
          }
        );
      },
      error => {
        console.error('Error loading income categories:', error);
        this.errorMessage = 'Failed to load categories';
      }
    );
  }

  add() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.newTx.description || !this.newTx.amount || !this.newTx.category || !this.newTx.date) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }

    if (this.newTx.amount <= 0) {
      this.errorMessage = 'Amount must be greater than 0';
      return;
    }

    this.addingTransaction = true;
    this.loaderComponent.showLoading('Adding transaction...');

    this.tx.create(this.newTx as Transaction).subscribe(
      () => {
        this.addingTransaction = false;
        
        // Store transaction details before resetting
        const txType = this.newTx.type;
        const txAmount = this.newTx.amount;
        const txCategory = this.newTx.category;
        const txDescription = this.newTx.description;
        const txDate = this.newTx.date;
        const sendSMS = this.sendSMSNotification;
        
        // Reset form
        this.newTx = { type: TransactionType.EXPENSE };
        this.sendSMSNotification = false;
        
        // Show success message from loader
        this.loaderComponent.showSuccess('✓ Transaction added successfully!');
        
        // Load recent and summary in parallel (don't wait)
        this.loadRecent();
        this.loadSummary();
        
        // Send SMS notification asynchronously without blocking
        if (sendSMS && this.userPhone) {
          const message = `${txType} of ₹${txAmount} added. Balance: ₹${this.summary?.currentBalance || 0}`;
          this.notificationService.sendSMS(this.userPhone, message).subscribe(
            () => console.log('SMS sent successfully'),
            error => console.error('Error sending SMS:', error)
          );
        }
        
        // Note: Email notifications are sent by the backend via NotificationService.notifyTransaction()
        // This avoids sending duplicate emails
      },
      error => {
        this.addingTransaction = false;
        console.error('Error adding transaction:', error);
        if (error.status === 401) {
          this.loaderComponent.showError('❌ Session expired. Please login again.');
        } else {
          this.loaderComponent.showError('❌ Failed to add transaction');
        }
      }
    );
  }

  deleteTransaction(id: number | undefined) {
    if (!id) return;

    if (!confirm('Are you sure you want to delete this transaction?')) {
      return;
    }

    this.deletingTransactionId = id;
    this.errorMessage = '';

    this.tx.delete(id).subscribe(
      () => {
        this.deletingTransactionId = undefined;
        this.successMessage = 'Transaction deleted successfully!';
        this.loadRecent();
        this.loadSummary();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error => {
        this.deletingTransactionId = undefined;
        console.error('Error deleting transaction:', error);
        if (error.status === 401) {
          this.errorMessage = 'Session expired. Please login again.';
        } else {
          this.errorMessage = 'Failed to delete transaction. Please try again.';
        }
      }
    );
  }
}