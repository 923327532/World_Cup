import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { User } from '../../models/user.model';

export interface NotificationItem {
  id: number;
  userId: number;
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  constructor(private http: HttpClient) {}

  getUnreadNotifications(userId: number): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(`${API_ENDPOINTS.notifications}/user/${userId}/unread`, { context: this.silentContext() }).pipe(catchError(() => of([])));
  }

  getUserNotifications(userId: number): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(`${API_ENDPOINTS.notifications}/user/${userId}`, { context: this.silentContext() }).pipe(catchError(() => of([])));
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${API_ENDPOINTS.notifications}/${id}/read`, {}, { context: this.silentContext() }).pipe(catchError(() => of(void 0)));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
