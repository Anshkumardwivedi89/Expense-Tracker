import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Category, CategoryType } from '../models/category';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  constructor(private http: HttpClient) {}

  get(type: CategoryType): Observable<Category[]> {
    return this.http.get<Category[]>(`${environment.apiUrl}/categories?type=${type}`);
  }

  create(category: Category): Observable<Category> {
    return this.http.post<Category>(`${environment.apiUrl}/categories`, category);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/categories/${id}`);
  }
}