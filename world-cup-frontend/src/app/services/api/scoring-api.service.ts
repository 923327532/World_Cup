import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { ScoreHistoryDTO, UserScoreDTO } from '../../models/ranking.model';

@Injectable({ providedIn: 'root' })
export class ScoringApiService {
  constructor(private http: HttpClient) {}

  getUserScore(userId: number): Observable<UserScoreDTO> {
    return this.http
      .get<UserScoreDTO>(`${API_ENDPOINTS.scoring}/user/${userId}`, {
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({ userId, totalPoints: 0, correctPredictions: 0, totalPredictions: 0 }),
        ),
      );
  }

  getUserHistory(userId: number): Observable<ScoreHistoryDTO[]> {
    return this.http
      .get<
        ScoreHistoryDTO[]
      >(`${API_ENDPOINTS.scoring}/user/${userId}/history`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
