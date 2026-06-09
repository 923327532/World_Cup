import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, throwError } from 'rxjs';
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

  private handleAuthError(error: unknown): Observable<never> {
    const fallbackMessage = 'No se pudo conectar con el servidor. Revisa tu conexión e inténtalo de nuevo.';
    if (error instanceof Error) {
      return throwError(() => new Error(error.message || fallbackMessage));
    }

    const httpError = error as { status?: number; error?: { message?: string } };
    const serverMessage = httpError.error?.message;
    if (httpError.status === 0) {
      return throwError(() => new Error(fallbackMessage));
    }

    return throwError(() => new Error(serverMessage || 'Credenciales inválidas o servidor no disponible.'));
  }

  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_ENDPOINTS.auth}/login`, payload).pipe(catchError((error) => this.handleAuthError(error)));
  }

  register(payload: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_ENDPOINTS.auth}/register`, payload).pipe(catchError((error) => this.handleAuthError(error)));
  }

  verifyEmail(payload: VerifyEmailRequest): Observable<boolean> {
    return this.http.post<boolean>(`${API_ENDPOINTS.auth}/verify-email`, payload).pipe(catchError((error) => this.handleAuthError(error)));
  }
}
