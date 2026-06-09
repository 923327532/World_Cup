import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import {
  CreatePredictionRequest,
  PredictionDTO,
  PredictionTypeDTO,
  UpdatePredictionRequest,
} from '../../models/prediction.model';
import { MOCK_PREDICTIONS } from './mock-data';
import { AuthService } from '../../core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class PredictionApiService {
  private readonly localPredictions: PredictionDTO[] = [...MOCK_PREDICTIONS];

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  getMyPredictions(): Observable<PredictionDTO[]> {
    const userId = this.authService.getCurrentUser()?.userId;
    if (!userId) return of(this.localPredictions);
    return this.http
      .get<
        PredictionDTO[]
      >(`${API_ENDPOINTS.predictions}/user/${userId}`, { context: this.silentContext() })
      .pipe(catchError(() => of(this.localPredictions)));
  }

  getMatchPredictions(matchId: number): Observable<PredictionDTO[]> {
    return this.http
      .get<
        PredictionDTO[]
      >(`${API_ENDPOINTS.predictions}/match/${matchId}`, { context: this.silentContext() })
      .pipe(catchError(() => of(this.localPredictions.filter((p) => p.matchId === matchId))));
  }

  createPrediction(payload: CreatePredictionRequest): Observable<PredictionDTO> {
    const userId = this.authService.getCurrentUser()?.userId || 0;
    const saved: PredictionDTO = {
      id: Date.now(),
      userId,
      matchId: payload.matchId,
      roomId: payload.roomId,
      homeTeam: '',
      awayTeam: '',
      predictionTypeId: payload.predictionTypeId,
      predictionType: '',
      predictionValue: payload.predictionValue,
      points: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      isLocked: false,
    };
    this.localPredictions.unshift(saved);
    return this.http
      .post<PredictionDTO>(API_ENDPOINTS.predictions, payload, { context: this.silentContext() })
      .pipe(catchError(() => of(saved)));
  }

  updatePrediction(id: number, payload: UpdatePredictionRequest): Observable<PredictionDTO> {
    return this.http
      .put<PredictionDTO>(`${API_ENDPOINTS.predictions}/${id}`, payload, {
        context: this.silentContext(),
      })
      .pipe(
        catchError(() => {
          const updated = this.localPredictions.find((item) => item.id === id);
          if (updated) {
            updated.predictionValue = payload.predictionValue;
            return of(updated);
          }
          return of(this.localPredictions[0]);
        }),
      );
  }

  getPredictionTypes(): Observable<PredictionTypeDTO[]> {
    return this.http
      .get<PredictionTypeDTO[]>(API_ENDPOINTS.predictionTypes, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of([
            { id: 1, code: 'SCORE', name: 'Marcador exacto', points: 8 },
            { id: 2, code: 'WINNER', name: 'Ganador', points: 5 },
          ]),
        ),
      );
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
