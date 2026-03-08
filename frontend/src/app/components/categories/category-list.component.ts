import { Component, OnInit } from '@angular/core';
import { CategoryService } from '../../services/category.service';
import { Category, CategoryType } from '../../models/category';

@Component({
  selector: 'app-category-list',
  template: `
    <div class="category-container">
      <div class="header">
        <h2>Manage Categories</h2>
        <a routerLink="/" class="btn-back">Back to Dashboard</a>
      </div>

      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div *ngIf="successMessage" class="success-message">
        {{ successMessage }}
      </div>

      <div class="category-card">
        <div class="filter-section">
          <label>Category Type:
            <select [(ngModel)]="type" (change)="load()">
              <option [value]="categoryType.EXPENSE">Expense</option>
              <option [value]="categoryType.INCOME">Income</option>
            </select>
          </label>
        </div>

        <div class="categories-list">
          <h3>{{ type }} Categories</h3>
          <div *ngIf="categories.length === 0" class="no-categories">
            No categories found. Add one below.
          </div>
          <ul *ngIf="categories.length > 0">
            <li *ngFor="let c of categories" class="category-item">
              <span class="category-name">{{ c.name }}</span>
              <button (click)="remove(c.id)" class="btn-delete" [disabled]="deletingId === c.id">
                {{ deletingId === c.id ? 'Deleting...' : 'Delete' }}
              </button>
            </li>
          </ul>
        </div>

        <div class="add-category-section">
          <h3>Add New Category</h3>
          <div class="add-form">
            <input 
              [(ngModel)]="newName" 
              placeholder="Enter category name"
              [readonly]="isAdding"
              (keyup.enter)="add()">
            <button (click)="add()" [disabled]="isAdding" class="btn-add">
              {{ isAdding ? 'Adding...' : 'Add' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .category-container {
      padding: 2rem;
      max-width: 600px;
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

    .category-card {
      background: white;
      padding: 1.5rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .filter-section {
      margin-bottom: 2rem;
    }

    .filter-section label {
      display: flex;
      flex-direction: column;
      color: #555;
      font-weight: 500;
    }

    .filter-section select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      margin-top: 0.5rem;
      font-size: 1rem;
    }

    .categories-list {
      margin-bottom: 2rem;
    }

    .categories-list h3 {
      color: #333;
      margin-bottom: 1rem;
    }

    .no-categories {
      padding: 1rem;
      background-color: #f9f9f9;
      border-radius: 4px;
      color: #666;
      text-align: center;
    }

    ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .category-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem;
      background-color: #f9f9f9;
      border-left: 4px solid #4CAF50;
      margin-bottom: 0.5rem;
      border-radius: 4px;
    }

    .category-name {
      font-weight: 500;
      color: #333;
    }

    .btn-delete {
      padding: 0.4rem 0.8rem;
      background-color: #dc3545;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s;
      font-size: 0.85rem;
    }

    .btn-delete:hover:not(:disabled) {
      background-color: #c82333;
    }

    .btn-delete:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }

    .add-category-section {
      border-top: 1px solid #ddd;
      padding-top: 1.5rem;
    }

    .add-category-section h3 {
      color: #333;
      margin-bottom: 1rem;
    }

    .add-form {
      display: flex;
      gap: 0.5rem;
    }

    .add-form input {
      flex: 1;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
    }

    .add-form input:focus {
      outline: none;
      border-color: #4CAF50;
      box-shadow: 0 0 5px rgba(76,175,80,0.3);
    }

    .btn-add {
      padding: 0.75rem 1.5rem;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s;
      font-weight: 500;
    }

    .btn-add:hover:not(:disabled) {
      background-color: #45a049;
    }

    .btn-add:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }
  `]
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  type: CategoryType = CategoryType.EXPENSE;
  newName = '';
  isAdding = false;
  deletingId: number | undefined;
  errorMessage = '';
  successMessage = '';
  categoryType = CategoryType;

  constructor(private service: CategoryService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.service.get(this.type).subscribe(
      data => {
        this.categories = data;
        this.errorMessage = '';
      },
      error => {
        console.error('Error loading categories:', error);
        this.errorMessage = 'Failed to load categories';
        this.categories = [];
      }
    );
  }

  add() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.newName.trim()) {
      this.errorMessage = 'Please enter a category name';
      return;
    }

    if (this.newName.length > 50) {
      this.errorMessage = 'Category name is too long (max 50 characters)';
      return;
    }

    this.isAdding = true;

    this.service.create({ name: this.newName, type: this.type })
      .subscribe(
        () => {
          this.isAdding = false;
          this.successMessage = 'Category added successfully!';
          this.newName = '';
          this.load();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error => {
          this.isAdding = false;
          console.error('Error adding category:', error);
          if (error.status === 409) {
            this.errorMessage = 'This category already exists';
          } else if (error.status === 401) {
            this.errorMessage = 'Session expired. Please login again.';
          } else {
            this.errorMessage = 'Failed to add category. Please try again.';
          }
        }
      );
  }

  remove(id: number | undefined) {
    if (!id) return;

    if (!confirm('Are you sure you want to delete this category?')) {
      return;
    }

    this.deletingId = id;
    this.errorMessage = '';

    this.service.delete(id).subscribe(
      () => {
        this.deletingId = undefined;
        this.successMessage = 'Category deleted successfully!';
        this.load();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error => {
        this.deletingId = undefined;
        console.error('Error deleting category:', error);
        if (error.status === 401) {
          this.errorMessage = 'Session expired. Please login again.';
        } else {
          this.errorMessage = 'Failed to delete category. Please try again.';
        }
      }
    );
  }
}