import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, map, of, tap } from 'rxjs';
import { AuthResponse, User } from '../../models/user.model';
import { StorageService } from './storage.service';
import { AuthApiService, LoginRequest, RegisterRequest, VerifyEmailRequest } from '../../services/api/auth-api.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storage = inject(StorageService);
  private readonly authApiService = inject(AuthApiService);
  private readonly tokenKey = 'worldCupToken';
  private readonly userKey = 'worldCupUser';
  private readonly userSubject = new BehaviorSubject<User | null>(this.normalizeUser(this.storage.get<User & { id?: number; username?: string }>(this.userKey)));
  readonly user$ = this.userSubject.asObservable();

  login(email: string, password: string): Observable<AuthResponse> {
    const payload: LoginRequest = { email, password };
    return this.authApiService.login(payload).pipe(
      tap((auth) => this.persist({
        ...auth,
        type: auth.type || 'Bearer'
      }))
    );
  }

  register(firstName: string, lastName: string, email: string, password: string, role: 'STUDENT' | 'TEACHER', studentCode?: string): Observable<AuthResponse> {
    const payload: RegisterRequest = { firstName, lastName, email, password, role, studentCode };
    return this.authApiService.register(payload).pipe(tap((auth) => this.persist(auth)));
  }

  verifyEmail(email: string, token: string): Observable<boolean> {
    const payload: VerifyEmailRequest = { email, token };
    return this.authApiService.verifyEmail(payload);
  }

  logout(): void {
    this.storage.remove(this.tokenKey);
    this.storage.remove(this.userKey);
    this.userSubject.next(null);
  }

  getToken(): string | null {
    return this.storage.get<string>(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  hasRole(role: User['role']): boolean {
    return this.userSubject.value?.role === role;
  }

  getCurrentUser(): User | null {
    return this.userSubject.value;
  }

  private persist(auth: AuthResponse): void {
    this.storage.set(this.tokenKey, auth.token);
    const user: User = {
      userId: auth.userId,
      email: auth.email,
      role: auth.role as User['role']
    };
    this.storage.set(this.userKey, user);
    this.userSubject.next(user);
  }

  private normalizeUser(user: (User & { id?: number; username?: string }) | null): User | null {
    if (!user) return null;
    return {
      userId: user.userId || user.id || 0,
      email: user.email,
      firstName: user.firstName || user.username,
      lastName: user.lastName,
      role: ((user.role as string) === 'USER' ? 'STUDENT' : user.role) as User['role']
    } as User;
  }
}
