import { Injectable } from '@angular/core';
import { HttpContextToken, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { ErrorHandlerService } from '../services/error-handler.service';
import { NotificationService } from '../services/notification.service';

export const SILENT_ERROR = new HttpContextToken(() => false);

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private errorHandler: ErrorHandlerService,
    private notificationService: NotificationService
  ) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error) => {
        if (!req.context.get(SILENT_ERROR)) {
          this.notificationService.error(this.errorHandler.getMessage(error));
        }
        return throwError(() => error);
      })
    );
  }
}
