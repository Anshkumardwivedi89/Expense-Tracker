import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Transaction } from '../models/transaction';

@Injectable({ providedIn: 'root' })
export class TransactionService {
  constructor(private http: HttpClient) {}

  recent(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${environment.apiUrl}/transactions/recent`);
  }

  create(tx: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(`${environment.apiUrl}/transactions`, tx);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/transactions/${id}`);
  }
}