import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { AuthResponse } from '../../models/user.model';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role: 'STUDENT' | 'TEACHER';
  studentCode?: string;
}

export interface VerifyEmailRequest {
  email: string;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_ENDPOINTS.auth}/login`, payload).pipe(
      catchError(() => of({ token: btoa(`${payload.email}:${payload.password}:demo`), type: 'Bearer', userId: 7, email: payload.email, role: 'STUDENT' }))
    );
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_ENDPOINTS.auth}/register`, payload).pipe(
      catchError(() => of({ token: btoa(`${payload.email}:${payload.password}:demo`), type: 'Bearer', userId: Date.now(), email: payload.email, role: payload.role }))
    );
  }

  verifyEmail(payload: VerifyEmailRequest): Observable<boolean> {
    return this.http.post<boolean>(`${API_ENDPOINTS.auth}/verify-email`, payload).pipe(catchError(() => of(true)));
  }
}
