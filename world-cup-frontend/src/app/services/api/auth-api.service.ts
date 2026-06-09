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

    const httpError = error as { status?: number; error?: { message?: string } | string };
    const serverMessage =
      typeof httpError.error === 'string'
        ? httpError.error
        : httpError.error?.message;

    if (httpError.status === 0) {
      return throwError(() => new Error(fallbackMessage));
    }

    if (httpError.status === 401) {
      return throwError(() => new Error(serverMessage || 'Credenciales inválidas. Verifica tu correo y contraseña.'));
    }

    if (httpError.status === 403) {
      return throwError(() => new Error(serverMessage || 'No se pudo iniciar sesion con esos datos. Verifica correo y contrasena o registra una cuenta nueva.'));
    }

    if (httpError.status === 409) {
      return throwError(() => new Error(serverMessage || 'El correo ya está registrado. Inicia sesión o usa otro correo.'));
    }

    return throwError(() => new Error(serverMessage || `Error ${httpError.status || ''}`.trim()));
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
