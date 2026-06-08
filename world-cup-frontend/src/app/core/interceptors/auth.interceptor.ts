import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (req.url.includes('/auth/')) {
      return next.handle(req);
    }

    const token = this.authService.getToken();
    const currentUser = this.authService.getCurrentUser();
    const headers: Record<string, string> = {};

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    if (currentUser?.userId) {
      headers['X-User-Id'] = String(currentUser.userId);
    }

    const request = Object.keys(headers).length ? req.clone({ setHeaders: headers }) : req;
    return next.handle(request);
  }
}
