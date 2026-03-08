import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardSummary {
  totalIncome: number;
  totalExpense: number;
  currentBalance: number;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  getSummary(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>(`${environment.apiUrl}/dashboard/summary`);
  }

  getWeeklyReport(month: number, year: number): Observable<any> {
    return this.http.get(`${environment.apiUrl}/reports/income-vs-expense`, {
      params: { month: month.toString(), year: year.toString() }
    });
  }
}
