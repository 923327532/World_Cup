# README 02: Core - Módulos Core (Guards, Interceptors, Services)

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar los módulos core que serán utilizados en toda la aplicación: guards, interceptors, servicios globales y constantes.

---

## 1. ESTRUCTURA DEL MÓDULO CORE

```
core
│
├── guards
│   ├── auth.guard.ts
│   └── admin.guard.ts
│
├── interceptors
│   ├── auth.interceptor.ts
│   ├── error.interceptor.ts
│   └── loading.interceptor.ts
│
├── services
│   ├── auth.service.ts
│   ├── websocket.service.ts
│   ├── notification.service.ts
│   └── storage.service.ts
│
├── constants
│   └── api.constants.ts
│
└── core.module.ts
```

---

## 2. GUARDS

### guards/auth.guard.ts
```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    const token = this.authService.getToken();
    
    if (token && !this.authService.isTokenExpired(token)) {
      return true;
    }
    
    this.router.navigate(['/auth/login']);
    return false;
  }
}
```

### guards/admin.guard.ts
```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  canActivate(): Observable<boolean> | Promise<boolean> | boolean {
    const user = this.authService.getCurrentUser();
    
    if (user && user.role === 'ADMIN') {
      return true;
    }
    
    this.router.navigate(['/dashboard']);
    return false;
  }
}
```

---

## 3. INTERCEPTORS

### interceptors/auth.interceptor.ts
```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}
  
  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();
    
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(request);
  }
}
```

### interceptors/error.interceptor.ts
```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class ErrorInterceptor implements HttpInterceptor {
  
  constructor(private snackBar: MatSnackBar) {}
  
  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An error occurred';
        
        if (error.error instanceof ErrorEvent) {
          errorMessage = `Error: ${error.error.message}`;
        } else {
          errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        }
        
        this.snackBar.open(errorMessage, 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        
        return throwError(() => error);
      })
    );
  }
}
```

### interceptors/loading.interceptor.ts
```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { LoadingService } from '../services/loading.service';

@Injectable({
  providedIn: 'root'
})
export class LoadingInterceptor implements HttpInterceptor {
  
  private totalRequests = 0;
  
  constructor(private loadingService: LoadingService) {}
  
  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    this.totalRequests++;
    this.loadingService.setLoading(true);
    
    return next.handle(request).pipe(
      finalize(() => {
        this.totalRequests--;
        if (this.totalRequests === 0) {
          this.loadingService.setLoading(false);
        }
      })
    );
  }
}
```

---

## 4. SERVICES

### services/auth.service.ts
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { StorageService } from './storage.service';

interface LoginResponse {
  token: string;
  type: string;
  userId: number;
  email: string;
  role: string;
}

interface User {
  id: number;
  email: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  constructor(
    private http: HttpClient,
    private storageService: StorageService
  ) {
    this.loadUserFromStorage();
  }
  
  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, {
      email,
      password
    }).pipe(
      tap(response => {
        this.storageService.setItem('token', response.token);
        this.storageService.setItem('user', JSON.stringify({
          id: response.userId,
          email: response.email,
          role: response.role
        }));
        this.currentUserSubject.next({
          id: response.userId,
          email: response.email,
          role: response.role
        });
      })
    );
  }
  
  register(data: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/auth/register`, data);
  }
  
  verifyEmail(email: string, token: string): Observable<boolean> {
    return this.http.post<boolean>(`${environment.apiUrl}/auth/verify-email`, {
      email,
      token
    });
  }
  
  logout(): void {
    this.storageService.removeItem('token');
    this.storageService.removeItem('user');
    this.currentUserSubject.next(null);
  }
  
  getToken(): string | null {
    return this.storageService.getItem('token');
  }
  
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
  
  isTokenExpired(token: string): boolean {
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return true;
    
    const expirationDate = new Date(decoded.exp * 1000);
    return expirationDate < new Date();
  }
  
  private decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch {
      return null;
    }
  }
  
  private loadUserFromStorage(): void {
    const userStr = this.storageService.getItem('user');
    if (userStr) {
      this.currentUserSubject.next(JSON.parse(userStr));
    }
  }
}
```

### services/websocket.service.ts
```typescript
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  
  private stompClient: any;
  private connected = false;
  
  constructor() {}
  
  connect(): void {
    if (this.connected) return;
    
    const socket = new SockJS('http://localhost:8087/ws/chat');
    this.stompClient = Stomp.over(socket);
    
    this.stompClient.connect({}, () => {
      this.connected = true;
      console.log('WebSocket connected');
    }, (error: any) => {
      console.error('WebSocket error:', error);
      this.connected = false;
    });
  }
  
  disconnect(): void {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect();
      this.connected = false;
    }
  }
  
  subscribeToMatchChat(matchId: number): Observable<any> {
    const subject = new Subject<any>();
    
    if (this.connected) {
      this.stompClient.subscribe(`/topic/chat/${matchId}`, (message: any) => {
        subject.next(JSON.parse(message.body));
      });
    }
    
    return subject.asObservable();
  }
  
  sendMatchComment(matchId: number, comment: any): void {
    if (this.connected) {
      this.stompClient.send(`/app/chat/${matchId}`, {}, JSON.stringify(comment));
    }
  }
  
  subscribeToNotifications(userId: number): Observable<any> {
    const subject = new Subject<any>();
    
    if (this.connected) {
      this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
        subject.next(JSON.parse(message.body));
      });
    }
    
    return subject.asObservable();
  }
}
```

### services/notification.service.ts
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface Notification {
  id: number;
  userId: number;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  
  constructor(private http: HttpClient) {}
  
  getUserNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${environment.apiUrl}/notifications/user/${userId}`);
  }
  
  markAsRead(notificationId: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/notifications/${notificationId}/read`, {});
  }
}
```

### services/storage.service.ts
```typescript
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  
  setItem(key: string, value: string): void {
    localStorage.setItem(key, value);
  }
  
  getItem(key: string): string | null {
    return localStorage.getItem(key);
  }
  
  removeItem(key: string): void {
    localStorage.removeItem(key);
  }
  
  clear(): void {
    localStorage.clear();
  }
}
```

### services/loading.service.ts
```typescript
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();
  
  setLoading(loading: boolean): void {
    this.loadingSubject.next(loading);
  }
}
```

---

## 5. CONSTANTS

### constants/api.constants.ts
```typescript
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    VERIFY_EMAIL: '/auth/verify-email'
  },
  MATCHES: {
    GET_BY_DATE: '/matches/date',
    GET_BY_ID: '/matches'
  },
  PREDICTIONS: {
    CREATE: '/predictions',
    GET_USER: '/predictions/user',
    GET_MATCH: '/predictions/match'
  },
  RANKINGS: {
    GLOBAL: '/leaderboard/global',
    CAMPUS: '/leaderboard/campus',
    CAREER: '/leaderboard/career',
    DEPARTMENT: '/leaderboard/department'
  },
  COMMENTS: {
    CREATE: '/comments',
    GET_MATCH: '/comments/match'
  },
  BADGES: {
    GET_ALL: '/badges',
    GET_USER: '/badges/user'
  },
  NOTIFICATIONS: {
    GET_USER: '/notifications/user',
    MARK_READ: '/notifications'
  }
};
```

---

## 6. CORE MODULE

### core/core.module.ts
```typescript
import { NgModule, ModuleWithProviders } from '@angular/core';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { ErrorInterceptor } from './interceptors/error.interceptor';
import { LoadingInterceptor } from './interceptors/loading.interceptor';

@NgModule({
  imports: [
    HttpClientModule
  ]
})
export class CoreModule {
  
  static forRoot(): ModuleWithProviders<CoreModule> {
    return {
      ngModule: CoreModule,
      providers: [
        {
          provide: HTTP_INTERCEPTORS,
          useClass: AuthInterceptor,
          multi: true
        },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: ErrorInterceptor,
          multi: true
        },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: LoadingInterceptor,
          multi: true
        }
      ]
    };
  }
}
```

---

## 7. APP.MODULE ACTUALIZADO

### app/app.module.ts
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './shared/material/material.module';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MaterialModule,
    CoreModule.forRoot(),
    SharedModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

---

## TAREA SIGUIENTE

Una vez implementados los módulos core, procede al README-03-shared.md para implementar los componentes compartidos.
