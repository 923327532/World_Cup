import { HttpClient, HttpContext, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import {
  AdminAuditLogResponse,
  ManualMatchRequest,
  ManualMatchResponse,
  MatchResultRequest,
} from '../../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminApiService {
  constructor(private http: HttpClient) {}

  private adminHeaders(): HttpHeaders {
    const userId = localStorage.getItem('userId') || '';
    const adminEmail = localStorage.getItem('adminEmail') || '';
    return new HttpHeaders().set('X-User-Id', userId).set('X-Admin-Email', adminEmail);
  }

  getMatches(): Observable<ManualMatchResponse[]> {
    return this.http
      .get<
        ManualMatchResponse[]
      >(`${API_ENDPOINTS.admin}/matches`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getMatchById(id: number): Observable<ManualMatchResponse> {
    return this.http
      .get<ManualMatchResponse>(`${API_ENDPOINTS.admin}/matches/${id}`, {
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({
            id,
            homeTeam: '',
            awayTeam: '',
            startTime: '',
            status: 'SCHEDULED',
            sourceType: 'MANUAL',
            createdBy: '',
            updatedBy: '',
            createdAt: '',
            updatedAt: '',
          }),
        ),
      );
  }

  getMatchesByStatus(status: string): Observable<ManualMatchResponse[]> {
    return this.http
      .get<
        ManualMatchResponse[]
      >(`${API_ENDPOINTS.admin}/matches/status/${status}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  createMatch(match: ManualMatchRequest): Observable<ManualMatchResponse> {
    return this.http
      .post<ManualMatchResponse>(`${API_ENDPOINTS.admin}/matches`, match, {
        headers: this.adminHeaders(),
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({
            id: Date.now(),
            ...match,
            status: 'SCHEDULED',
            sourceType: 'MANUAL',
            createdBy: '',
            updatedBy: '',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          }),
        ),
      );
  }

  updateMatch(id: number, match: ManualMatchRequest): Observable<ManualMatchResponse> {
    return this.http
      .put<ManualMatchResponse>(`${API_ENDPOINTS.admin}/matches/${id}`, match, {
        headers: this.adminHeaders(),
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({
            id,
            ...match,
            status: 'SCHEDULED',
            sourceType: 'MANUAL',
            createdBy: '',
            updatedBy: '',
            createdAt: '',
            updatedAt: '',
          }),
        ),
      );
  }

  updateMatchResult(id: number, result: MatchResultRequest): Observable<ManualMatchResponse> {
    return this.http
      .put<ManualMatchResponse>(`${API_ENDPOINTS.admin}/matches/${id}/result`, result, {
        headers: this.adminHeaders(),
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({
            id,
            homeTeam: '',
            awayTeam: '',
            startTime: '',
            status: 'FINISHED',
            ...result,
            sourceType: 'MANUAL',
            createdBy: '',
            updatedBy: '',
            createdAt: '',
            updatedAt: '',
          }),
        ),
      );
  }

  updateMatchStatus(id: number, status: string): Observable<ManualMatchResponse> {
    return this.http
      .patch<ManualMatchResponse>(`${API_ENDPOINTS.admin}/matches/${id}/status`, null, {
        headers: this.adminHeaders(),
        params: { status },
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({
            id,
            homeTeam: '',
            awayTeam: '',
            startTime: '',
            status,
            sourceType: 'MANUAL',
            createdBy: '',
            updatedBy: '',
            createdAt: '',
            updatedAt: '',
          }),
        ),
      );
  }

  deleteMatch(id: number): Observable<void> {
    return this.http
      .delete<void>(`${API_ENDPOINTS.admin}/matches/${id}`, {
        headers: this.adminHeaders(),
        context: this.silentContext(),
      })
      .pipe(catchError(() => of(void 0)));
  }

  getAuditLogs(): Observable<AdminAuditLogResponse[]> {
    return this.http
      .get<
        AdminAuditLogResponse[]
      >(`${API_ENDPOINTS.admin}/audit`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getAuditLogsByAdmin(adminId: number): Observable<AdminAuditLogResponse[]> {
    return this.http
      .get<
        AdminAuditLogResponse[]
      >(`${API_ENDPOINTS.admin}/audit/admin/${adminId}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getAuditLogsByEntity(entityType: string, entityId: number): Observable<AdminAuditLogResponse[]> {
    return this.http
      .get<
        AdminAuditLogResponse[]
      >(`${API_ENDPOINTS.admin}/audit/entity`, { params: { entityType, entityId: entityId.toString() }, context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
