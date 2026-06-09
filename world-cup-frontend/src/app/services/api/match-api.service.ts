import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, delay, of, retry, tap } from 'rxjs';
import { API_ENDPOINTS, CACHE_TTL } from '../../core/constants/api.constants';
import { CacheService } from '../../core/services/cache.service';
import { Match } from '../../models/match.model';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';

@Injectable({ providedIn: 'root' })
export class MatchApiService {
  constructor(
    private http: HttpClient,
    private cache: CacheService,
  ) {}

  getMatchesByDate(date: string): Observable<Match[]> {
    return this.cached(`matches:${date}`, CACHE_TTL.scheduledMatches, () =>
      this.http
        .get<
          Match[]
        >(`${API_ENDPOINTS.worldcup}/matches/date/${date}`, { context: this.silentContext() })
        .pipe(catchError(() => of([]))),
    );
  }

  getAllMatches(): Observable<Match[]> {
    return this.cached('all-matches', CACHE_TTL.scheduledMatches, () =>
      this.http
        .get<Match[]>(`${API_ENDPOINTS.worldcup}/matches`, { context: this.silentContext() })
        .pipe(catchError(() => of([]))),
    );
  }

  getLiveMatches(): Observable<Match[]> {
    return this.cached('live-matches', CACHE_TTL.liveMatches, () =>
      this.http
        .get<
          Match[]
        >(`${API_ENDPOINTS.worldcup}/matches/live`, { context: this.silentContext() })
        .pipe(catchError(() => of([]))),
    );
  }

  getMatch(id: number): Observable<Match> {
    return this.http
      .get<Match>(`${API_ENDPOINTS.worldcup}/matches/${id}`, { context: this.silentContext() })
      .pipe(
        retry({ count: 3, delay: (_error, count) => of(count).pipe(delay(count * 1000)) }),
        catchError(() =>
          of({
            id,
            homeTeam: 'N/A',
            awayTeam: 'N/A',
            kickoffTime: new Date().toISOString(),
            status: 'SCHEDULED',
          }),
        ),
      );
  }

  private cached<T>(key: string, ttl: number, request: () => Observable<T>): Observable<T> {
    const cached = this.cache.get<T>(key, ttl);
    return cached ? of(cached) : request().pipe(tap((data) => this.cache.set(key, data)));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
