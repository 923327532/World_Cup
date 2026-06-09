import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { BadgeDTO, RewardDTO, UserBadgeDTO } from '../../models/ranking.model';
import { MOCK_BADGES } from './mock-data';
import { AuthService } from '../../core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class GamificationApiService {
  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  getBadges(): Observable<BadgeDTO[]> {
    return this.http
      .get<BadgeDTO[]>(API_ENDPOINTS.badges, { context: this.silentContext() })
      .pipe(catchError(() => of(MOCK_BADGES.map((b) => ({ ...b, criteria: '', points: 0 })))));
  }

  getUserBadges(): Observable<UserBadgeDTO[]> {
    const userId = this.authService.getCurrentUser()?.userId;
    if (!userId) return of([]);
    return this.http
      .get<
        UserBadgeDTO[]
      >(`${API_ENDPOINTS.badges}/user/${userId}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getRewards(): Observable<RewardDTO[]> {
    return this.http
      .get<RewardDTO[]>(API_ENDPOINTS.rewards, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of([
            {
              id: 1,
              name: 'Entrada a viewing party',
              description: 'Acceso exclusivo al evento final.',
              icon: 'emoji_events',
              requiredPoints: 100,
              isActive: true,
            },
            {
              id: 2,
              name: 'Merch oficial',
              description: 'Kit de premios Tecsup World Cup.',
              icon: 'card_giftcard',
              requiredPoints: 50,
              isActive: true,
            },
          ]),
        ),
      );
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
