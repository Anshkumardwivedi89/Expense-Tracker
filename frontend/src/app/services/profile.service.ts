import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserProfile } from '../models/user-profile';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private profileUpdated = new BehaviorSubject<UserProfile | null>(null);
  public profileUpdated$ = this.profileUpdated.asObservable();

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${environment.apiUrl}/profile`);
  }

  updateProfile(profile: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${environment.apiUrl}/profile`, profile);
  }

  notifyProfileUpdate(profile: UserProfile) {
    this.profileUpdated.next(profile);
  }
}
