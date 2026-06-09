import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_ENDPOINTS, CACHE_TTL } from '../../core/constants/api.constants';
import { CacheService } from '../../core/services/cache.service';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { LeaderboardEntryDTO, RankingEntry } from '../../models/ranking.model';
import { OrganizationApiService } from './organization-api.service';

@Injectable({ providedIn: 'root' })
export class RankingApiService {
  constructor(
    private http: HttpClient,
    private cache: CacheService,
    private orgApi: OrganizationApiService,
  ) {}

  getGlobalRanking(limit = 100): Observable<LeaderboardEntryDTO[]> {
    return this.http
      .get<
        LeaderboardEntryDTO[]
      >(`${API_ENDPOINTS.leaderboard}/global?limit=${limit}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getCampusRanking(campusId: number, limit = 50): Observable<LeaderboardEntryDTO[]> {
    return this.http
      .get<
        LeaderboardEntryDTO[]
      >(`${API_ENDPOINTS.leaderboard}/campus/${campusId}?limit=${limit}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getCareerRanking(careerId: number, limit = 50): Observable<LeaderboardEntryDTO[]> {
    return this.http
      .get<
        LeaderboardEntryDTO[]
      >(`${API_ENDPOINTS.leaderboard}/career/${careerId}?limit=${limit}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getDepartmentRanking(departmentId: number, limit = 50): Observable<LeaderboardEntryDTO[]> {
    return this.http
      .get<
        LeaderboardEntryDTO[]
      >(`${API_ENDPOINTS.leaderboard}/department/${departmentId}?limit=${limit}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
