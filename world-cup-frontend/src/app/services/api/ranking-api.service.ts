import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_ENDPOINTS, CACHE_TTL } from '../../core/constants/api.constants';
import { CacheService } from '../../core/services/cache.service';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { RankingEntry } from '../../models/ranking.model';
import { MOCK_RANKINGS } from './mock-data';
import { OrganizationApiService } from './organization-api.service';

@Injectable({ providedIn: 'root' })
export class RankingApiService {
  constructor(private http: HttpClient, private cache: CacheService, private orgApi: OrganizationApiService) {}

  getGlobalRanking(): Observable<RankingEntry[]> {
    const cached = this.cache.get<RankingEntry[]>('global-ranking', CACHE_TTL.rankings);
    if (cached) return of(cached);
    return this.http.get<RankingEntry[]>(`${API_ENDPOINTS.leaderboard}/global?limit=100`, { context: this.silentContext() }).pipe(
      catchError(() => of(MOCK_RANKINGS)),
      tap((ranking) => this.cache.set('global-ranking', ranking))
    );
  }

  getCampusRanking(campusId: number): Observable<RankingEntry[]> {
    return this.http.get<RankingEntry[]>(`${API_ENDPOINTS.leaderboard}/campus/${campusId}?limit=50`, { context: this.silentContext() }).pipe(catchError(() => of(MOCK_RANKINGS)));
  }

  getCareerRanking(careerId: number): Observable<RankingEntry[]> {
    return this.http.get<RankingEntry[]>(`${API_ENDPOINTS.leaderboard}/career/${careerId}?limit=50`, { context: this.silentContext() }).pipe(catchError(() => of(MOCK_RANKINGS)));
  }

  getDepartmentRanking(departmentId: number): Observable<RankingEntry[]> {
    return this.http.get<RankingEntry[]>(`${API_ENDPOINTS.leaderboard}/department/${departmentId}?limit=50`, { context: this.silentContext() }).pipe(catchError(() => of(MOCK_RANKINGS)));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
