import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { Prediction, PredictionType } from '../../models/prediction.model';
import { MOCK_PREDICTIONS } from './mock-data';
import { AuthService } from '../../core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class PredictionApiService {
  private readonly localPredictions: Prediction[] = [...MOCK_PREDICTIONS];

  constructor(private http: HttpClient, private authService: AuthService) {}

  getMyPredictions(): Observable<Prediction[]> {
    const userId = this.authService.getCurrentUser()?.userId;
    if (!userId) return of(this.localPredictions);
    return this.http.get<Prediction[]>(`${API_ENDPOINTS.predictions}/user/${userId}`, { context: this.silentContext() }).pipe(catchError(() => of(this.localPredictions)));
  }

  createPrediction(prediction: Prediction): Observable<Prediction> {
    const saved = { ...prediction, id: Date.now(), isLocked: false, userId: this.authService.getCurrentUser()?.userId };
    this.localPredictions.unshift(saved);
    return this.http.post<Prediction>(API_ENDPOINTS.predictions, prediction, { context: this.silentContext() }).pipe(catchError(() => of(saved)));
  }

  updatePrediction(id: number, predictionValue: string): Observable<Prediction> {
    return this.http.put<Prediction>(`${API_ENDPOINTS.predictions}/${id}`, predictionValue, { context: this.silentContext() }).pipe(
      catchError(() => {
        const updated = this.localPredictions.find((item) => item.id === id);
        if (updated) {
          updated.predictionValue = predictionValue;
          return of(updated);
        }
        return of(this.localPredictions[0]);
      })
    );
  }

  getPredictionTypes(): Observable<PredictionType[]> {
    return this.http.get<PredictionType[]>(API_ENDPOINTS.predictionTypes, { context: this.silentContext() }).pipe(
      catchError(() => of([
        { id: 1, code: 'SCORE', name: 'Marcador exacto', points: 8 },
        { id: 2, code: 'WINNER', name: 'Ganador', points: 5 }
      ]))
    );
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
