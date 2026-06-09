import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, delay, of, retry, tap } from 'rxjs';
import { API_ENDPOINTS, CACHE_TTL } from '../../core/constants/api.constants';
import { CacheService } from '../../core/services/cache.service';
import { Match } from '../../models/match.model';
import { MOCK_MATCHES } from './mock-data';
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
        .pipe(catchError(() => of(MOCK_MATCHES))),
    );
  }

  getLiveMatches(): Observable<Match[]> {
    return this.cached('live-matches', CACHE_TTL.liveMatches, () =>
      this.http
        .get<
          Match[]
        >(`${API_ENDPOINTS.worldcup}/matches/date/${new Date().toISOString().slice(0, 10)}`, { context: this.silentContext() })
        .pipe(catchError(() => of(MOCK_MATCHES.filter((m) => m.status === 'LIVE')))),
    );
  }

  getMatch(id: number): Observable<Match> {
    return this.http
      .get<Match>(`${API_ENDPOINTS.worldcup}/matches/${id}`, { context: this.silentContext() })
      .pipe(
        retry({ count: 3, delay: (_error, count) => of(count).pipe(delay(count * 1000)) }),
        catchError(() => of(MOCK_MATCHES.find((match) => match.id === id) || MOCK_MATCHES[0])),
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
