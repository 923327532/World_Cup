import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';

export interface Tournament {
  id: number;
  name: string;
  year: number;
  startDate: string;
  endDate: string;
}

@Injectable({ providedIn: 'root' })
export class WorldcupApiService {
  constructor(private http: HttpClient) {}

  getCurrentTournament(): Observable<Tournament> {
    return this.http.get<Tournament>(`${API_ENDPOINTS.worldcup}/tournaments/current`, { context: this.silentContext() }).pipe(
      catchError(() => of({ id: 1, name: 'World Cup 2026', year: 2026, startDate: '2026-06-11', endDate: '2026-07-19' }))
    );
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
