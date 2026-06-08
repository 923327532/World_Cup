import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';

export interface UserScore {
  userId: number;
  totalPoints: number;
}

export interface ScoreHistory {
  id: number;
  userId: number;
  points: number;
  reason: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class ScoringApiService {
  constructor(private http: HttpClient) {}

  getUserScore(userId: number): Observable<UserScore> {
    return this.http.get<UserScore>(`${API_ENDPOINTS.scoring}/user/${userId}`, { context: this.silentContext() }).pipe(catchError(() => of({ userId, totalPoints: 0 })));
  }

  getUserHistory(userId: number): Observable<ScoreHistory[]> {
    return this.http.get<ScoreHistory[]>(`${API_ENDPOINTS.scoring}/user/${userId}/history`, { context: this.silentContext() }).pipe(catchError(() => of([])));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}

