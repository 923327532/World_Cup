import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { Badge } from '../../models/ranking.model';
import { MOCK_BADGES } from './mock-data';
import { AuthService } from '../../core/services/auth.service';

export interface UserBadge {
  id: number;
  userId: number;
  badge: Badge;
  earnedAt: string;
}

export interface Reward {
  id: number;
  position: number;
  title: string;
  description: string;
}

@Injectable({ providedIn: 'root' })
export class GamificationApiService {
  constructor(private http: HttpClient, private authService: AuthService) {}

  getBadges(): Observable<Badge[]> {
    return this.http.get<Badge[]>(API_ENDPOINTS.badges, { context: this.silentContext() }).pipe(catchError(() => of(MOCK_BADGES)));
  }

  getUserBadges(): Observable<UserBadge[]> {
    const userId = this.authService.getCurrentUser()?.userId;
    if (!userId) return of([]);
    return this.http.get<UserBadge[]>(`${API_ENDPOINTS.badges}/user/${userId}`, { context: this.silentContext() }).pipe(catchError(() => of([])));
  }

  getRewards(): Observable<Reward[]> {
    return this.http.get<Reward[]>(API_ENDPOINTS.rewards, { context: this.silentContext() }).pipe(catchError(() => of([
      { id: 1, position: 1, title: 'Entrada a viewing party', description: 'Acceso exclusivo al evento final.' },
      { id: 2, position: 2, title: 'Merch oficial', description: 'Kit de premios Tecsup World Cup.' }
    ])));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
