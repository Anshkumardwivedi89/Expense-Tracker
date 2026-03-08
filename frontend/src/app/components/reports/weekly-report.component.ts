import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../services/dashboard.service';
import { ProfileService } from '../../services/profile.service';
import { NotificationService } from '../../services/notification.service';
import { LoaderComponent } from '../shared/loader.component';
import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

export interface WeeklyReportData {
  income: number;
  expense: number;
  balance: number;
  weekStart: string;
  weekEnd: string;
  month: number;
  year: number;
}

@Component({
  selector: 'app-weekly-report',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LoaderComponent],
  template: `
    <app-loader #loaderComponent [isLoading]="isLoading" [message]="loaderMessage"></app-loader>
    
    <div class="report-container">
      <div class="header">
        <h2>Weekly & Monthly Reports</h2>
        <a routerLink="/" class="btn-back">Back to Dashboard</a>
      </div>

      <div *ngIf="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div class="report-filters">
        <div class="filter-group">
          <label>Month:
            <select [(ngModel)]="selectedMonth" (change)="loadReport()">
              <option *ngFor="let m of months; let i = index" [value]="i + 1">
                {{ m }}
              </option>
            </select>
          </label>
        </div>
        <div class="filter-group">
          <label>Year:
            <select [(ngModel)]="selectedYear" (change)="loadReport()">
              <option *ngFor="let y of years" [value]="y">{{ y }}</option>
            </select>
          </label>
        </div>
      </div>

      <div class="report-card" #reportContent *ngIf="reportData">
        <div class="report-header">
          <h3>{{ months[selectedMonth - 1] }} {{ selectedYear }} Report</h3>
        </div>

        <div class="report-summary">
          <div class="report-item income">
            <span class="label">Total Income</span>
            <span class="amount">₹{{ reportData.income | number: '1.2-2' }}</span>
          </div>
          <div class="report-item expense">
            <span class="label">Total Expenses</span>
            <span class="amount">₹{{ reportData.expense | number: '1.2-2' }}</span>
          </div>
          <div class="report-item balance" [ngClass]="reportData.balance >= 0 ? 'positive' : 'negative'">
            <span class="label">Balance</span>
            <span class="amount">₹{{ reportData.balance | number: '1.2-2' }}</span>
          </div>
        </div>

        <div class="report-chart">
          <h4>Income vs Expenses</h4>
          <div class="chart-container">
            <div class="chart-bar">
              <div class="bar income-bar" [style.width.%]="getBarWidth(reportData.income)">
                <span>Income: ₹{{ reportData.income | number: '1.2-2' }}</span>
              </div>
            </div>
            <div class="chart-bar">
              <div class="bar expense-bar" [style.width.%]="getBarWidth(reportData.expense)">
                <span>Expense: ₹{{ reportData.expense | number: '1.2-2' }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="report-breakdown">
          <h4>Key Metrics</h4>
          <div class="metric">
            <span class="metric-label">Savings Rate:</span>
            <span class="metric-value">{{ calculateSavingsRate(reportData) }}%</span>
          </div>
          <div class="metric">
            <span class="metric-label">Expense Ratio:</span>
            <span class="metric-value">{{ calculateExpenseRatio(reportData) }}%</span>
          </div>
        </div>

        <div class="report-actions">
          <button (click)="downloadPDF()" class="btn-download">📥 Download PDF</button>
          <button (click)="shareReport()" class="btn-share">📧 Send via Email</button>
        </div>
      </div>

      <div *ngIf="!reportData" class="loading">
        Loading report...
      </div>
    </div>
  `,
  styles: [`
    .report-container {
      padding: 2rem;
      max-width: 1000px;
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

    .report-filters {
      display: flex;
      gap: 1rem;
      margin-bottom: 2rem;
    }

    .filter-group {
      flex: 1;
    }

    .filter-group label {
      display: flex;
      flex-direction: column;
      color: #555;
      font-weight: 500;
    }

    .filter-group select {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      margin-top: 0.5rem;
      font-size: 1rem;
    }

    .report-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }

    .report-header {
      text-align: center;
      margin-bottom: 2rem;
      border-bottom: 2px solid #667eea;
      padding-bottom: 1rem;
    }

    .report-header h3 {
      color: #333;
      margin: 0;
    }

    .report-summary {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
      margin-bottom: 2rem;
    }

    .report-item {
      padding: 1.5rem;
      border-radius: 8px;
      text-align: center;
    }

    .report-item.income {
      background-color: #d4edda;
    }

    .report-item.expense {
      background-color: #f8d7da;
    }

    .report-item.balance {
      background-color: #e2e3e5;
    }

    .report-item.balance.positive {
      background-color: #d4edda;
    }

    .report-item.balance.negative {
      background-color: #f8d7da;
    }

    .report-item .label {
      display: block;
      font-size: 0.9rem;
      color: #555;
      margin-bottom: 0.5rem;
    }

    .report-item .amount {
      display: block;
      font-size: 1.8rem;
      font-weight: bold;
      color: #333;
    }

    .report-chart {
      margin-bottom: 2rem;
      padding-bottom: 2rem;
      border-bottom: 1px solid #ddd;
    }

    .report-chart h4 {
      color: #333;
      margin-bottom: 1rem;
    }

    .chart-container {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .chart-bar {
      display: flex;
      height: 40px;
      background-color: #f9f9f9;
      border-radius: 4px;
      overflow: hidden;
      align-items: center;
    }

    .bar {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-weight: bold;
      border-radius: 4px;
      transition: width 0.3s ease;
    }

    .income-bar {
      background: linear-gradient(90deg, #28a745, #20c997);
    }

    .expense-bar {
      background: linear-gradient(90deg, #dc3545, #fd7e14);
    }

    .bar span {
      font-size: 0.85rem;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .report-breakdown {
      margin-bottom: 2rem;
      padding: 1rem;
      background-color: #f9f9f9;
      border-radius: 4px;
    }

    .report-breakdown h4 {
      color: #333;
      margin-top: 0;
      margin-bottom: 1rem;
    }

    .metric {
      display: flex;
      justify-content: space-between;
      padding: 0.75rem 0;
      border-bottom: 1px solid #e0e0e0;
    }

    .metric:last-child {
      border-bottom: none;
    }

    .metric-label {
      color: #555;
      font-weight: 500;
    }

    .metric-value {
      color: #667eea;
      font-weight: bold;
    }

    .report-actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
    }

    .btn-download, .btn-share {
      padding: 0.75rem 1.5rem;
      background-color: #4CAF50;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
      transition: background-color 0.3s;
    }

    .btn-share {
      background-color: #667eea;
    }

    .btn-download:hover {
      background-color: #45a049;
    }

    .btn-share:hover {
      background-color: #5568d3;
    }

    .loading {
      text-align: center;
      padding: 2rem;
      color: #666;
    }
  `]
})
export class WeeklyReportComponent implements OnInit {
  @ViewChild('reportContent') reportContent!: ElementRef;
  @ViewChild('loaderComponent') loaderComponent!: LoaderComponent;
  
  reportData: WeeklyReportData | null = null;
  selectedMonth: number;
  selectedYear: number;
  errorMessage = '';
  userEmail: string | null = null;
  isLoading = false;
  loaderMessage = '';
  
  months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  
  years: number[] = [];

  constructor(
    private dashboardService: DashboardService,
    private profileService: ProfileService,
    private notificationService: NotificationService
  ) {
    const today = new Date();
    this.selectedMonth = today.getMonth() + 1;
    this.selectedYear = today.getFullYear();
    this.initializeYears();
  }

  ngOnInit() {
    this.loadUserEmail();
    this.loadReport();
  }

  loadUserEmail() {
    this.profileService.getProfile().subscribe(
      profile => {
        this.userEmail = profile.email || null;
      },
      error => console.error('Error loading user email:', error)
    );
  }

  private initializeYears() {
    const currentYear = new Date().getFullYear();
    for (let i = currentYear - 5; i <= currentYear + 2; i++) {
      this.years.push(i);
    }
  }

  loadReport() {
    this.dashboardService.getWeeklyReport(this.selectedMonth, this.selectedYear).subscribe(
      data => {
        this.reportData = {
          income: data.income || 0,
          expense: data.expense || 0,
          balance: (data.income || 0) - (data.expense || 0),
          weekStart: '',
          weekEnd: '',
          month: this.selectedMonth,
          year: this.selectedYear
        };
        this.errorMessage = '';
      },
      error => {
        console.error('Error loading report:', error);
        this.errorMessage = 'Failed to load report data';
      }
    );
  }

  getBarWidth(amount: number): number {
    if (!this.reportData) return 0;
    const max = Math.max(this.reportData.income, this.reportData.expense);
    return max === 0 ? 0 : (amount / max) * 100;
  }

  calculateSavingsRate(data: WeeklyReportData): number {
    if (data.income === 0) return 0;
    return Math.round(((data.income - data.expense) / data.income) * 100);
  }

  calculateExpenseRatio(data: WeeklyReportData): number {
    if (data.income === 0) return 0;
    return Math.round((data.expense / data.income) * 100);
  }

  downloadPDF() {
    if (!this.reportContent) return;

    this.loaderComponent.showLoading('Generating PDF...');
    
    const element = this.reportContent.nativeElement;
    
    html2canvas(element, { 
      scale: 2,
      useCORS: true,
      logging: false,
      backgroundColor: '#ffffff'
    }).then(canvas => {
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4'
      });

      const imgWidth = 190;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      
      pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight);
      pdf.save(`expense-report-${this.months[this.selectedMonth - 1]}-${this.selectedYear}.pdf`);
      
      this.loaderComponent.showSuccess('📥 PDF downloaded successfully!');
    }).catch(error => {
      console.error('Error generating PDF:', error);
      this.loaderComponent.showError('❌ Failed to generate PDF');
    });
  }

  shareReport() {
    if (!this.userEmail) {
      this.loaderComponent.showError('❌ Please update your profile with email');
      return;
    }
    
    if (!this.reportContent) return;
    
    this.loaderComponent.showLoading('Generating report...');
    
    const element = this.reportContent.nativeElement;
    
    html2canvas(element, { 
      scale: 2,
      useCORS: true,
      logging: false,
      backgroundColor: '#ffffff'
    }).then(canvas => {
      this.loaderComponent.showLoading('Sending email...');
      
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4'
      });

      const imgWidth = 190;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      
      pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight);
      
      const subject = `Expense Report - ${this.months[this.selectedMonth - 1]} ${this.selectedYear}`;
      const income = (this.reportData?.income || 0).toLocaleString('en-IN', {maximumFractionDigits: 2});
      const expense = (this.reportData?.expense || 0).toLocaleString('en-IN', {maximumFractionDigits: 2});
      const balance = (this.reportData?.balance || 0).toLocaleString('en-IN', {maximumFractionDigits: 2});
      const savingsRate = this.calculateSavingsRate(this.reportData!);
      const expenseRatio = this.calculateExpenseRatio(this.reportData!);
      const timestamp = new Date().toLocaleDateString('en-IN') + ' at ' + new Date().toLocaleTimeString('en-IN');
      
      const body = `<!DOCTYPE html><html><head><meta charset="UTF-8"><style>
        body{font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;line-height:1.6;color:#333;margin:0;padding:0;}
        .container{max-width:600px;margin:0 auto;background:#f9f9f9;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);}
        .header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center;}
        .header h1{margin:0;font-size:28px;font-weight:600;}
        .period{font-size:14px;opacity:0.9;margin-top:5px;}
        .content{padding:30px;}
        .summary{display:grid;grid-template-columns:1fr 1fr 1fr;gap:15px;margin-bottom:30px;}
        .box{padding:15px;border-radius:8px;text-align:center;color:white;}
        .label{font-size:12px;font-weight:600;text-transform:uppercase;opacity:0.9;margin-bottom:5px;}
        .value{font-size:24px;font-weight:700;}
        .income{background:linear-gradient(135deg,#27ae60 0%,#229954 100%);}
        .expense{background:linear-gradient(135deg,#e74c3c 0%,#c0392b 100%);}
        .balance{background:linear-gradient(135deg,#3498db 0%,#2980b9 100%);}
        .section{margin-bottom:25px;}
        .title{font-size:14px;font-weight:700;color:#667eea;text-transform:uppercase;margin-bottom:10px;border-bottom:2px solid #667eea;padding-bottom:8px;}
        .row{display:flex;justify-content:space-between;padding:10px 0;border-bottom:1px solid #eee;}
        .row:last-child{border-bottom:none;}
        .rkey{color:#666;font-weight:500;}
        .rval{font-weight:700;color:#333;}
        .highlight{background:#fff9e6;padding:15px;border-left:4px solid #ffc107;border-radius:4px;margin-top:15px;}
        .footer{background:#f0f0f0;padding:20px;text-align:center;font-size:12px;color:#666;border-top:1px solid #ddd;}
      </style></head><body>
        <div class="container">
          <div class="header">
            <h1>💰 Expense Tracker Report</h1>
            <div class="period">${this.months[this.selectedMonth - 1]} ${this.selectedYear}</div>
          </div>
          <div class="content">
            <div class="summary">
              <div class="box income">
                <div class="label">💵 Total Income</div>
                <div class="value">₹${income}</div>
              </div>
              <div class="box expense">
                <div class="label">💸 Total Expense</div>
                <div class="value">₹${expense}</div>
              </div>
              <div class="box balance">
                <div class="label">💳 Balance</div>
                <div class="value">₹${balance}</div>
              </div>
            </div>
            <div class="section">
              <div class="title">📊 Key Metrics</div>
              <div class="row"><span class="rkey">Savings Rate:</span><span class="rval">${savingsRate}%</span></div>
              <div class="row"><span class="rkey">Expense Ratio:</span><span class="rval">${expenseRatio}%</span></div>
            </div>
            <div class="highlight">
              <strong>📎 Detailed PDF Report</strong><br>A detailed breakdown of your transactions and spending patterns is attached as PDF for your records.
            </div>
          </div>
          <div class="footer">
            <div>📧 This is an automated report from Expense Tracker</div>
            <div>💡 Track your expenses, achieve your goals!</div>
            <div style="margin-top:10px;font-size:11px;color:#999;">Generated: ${timestamp}</div>
          </div>
        </div>
      </body></html>`;
      
      this.notificationService.sendEmail(this.userEmail!, subject, body).subscribe(
        () => {
          this.loaderComponent.showSuccess('📧 Report sent successfully!');
        },
        error => {
          console.error('Error sending report:', error);
          this.loaderComponent.showError('❌ Failed to send report');
        }
      );
    }).catch(error => {
      console.error('Error generating PDF:', error);
      this.loaderComponent.showError('❌ Failed to generate report');
    });
  }
}
