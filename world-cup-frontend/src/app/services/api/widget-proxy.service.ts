import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { CacheService } from '../../core/services/cache.service';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';

@Injectable({ providedIn: 'root' })
export class WidgetProxyService {
  constructor(private http: HttpClient, private cache: CacheService) {}

  getWidgetData<T>(endpoint: string, params: Record<string, string | number>, ttl = 60_000): Observable<T | null> {
    const key = `widget:${endpoint}:${JSON.stringify(params)}`;
    const cached = this.cache.get<T>(key, ttl);
    if (cached) return of(cached);

    const httpParams = Object.entries(params).reduce((acc, [paramKey, value]) => acc.set(paramKey, String(value)), new HttpParams());
    return this.http.get<T>(`${API_ENDPOINTS.auth.replace('/auth', '')}/widget/${endpoint}`, {
      params: httpParams,
      context: new HttpContext().set(SILENT_ERROR, true)
    }).pipe(
      tap((data) => this.cache.set(key, data)),
      catchError(() => of(null))
    );
  }
}
