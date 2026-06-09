import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_ENDPOINTS, CACHE_TTL } from '../../core/constants/api.constants';
import { CacheService } from '../../core/services/cache.service';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { MatchDTO, PlayerDTO, StadiumDTO, TeamDTO, TournamentDTO } from '../../models/worldcup.model';

@Injectable({ providedIn: 'root' })
export class WorldcupApiService {
  constructor(
    private http: HttpClient,
    private cache: CacheService,
  ) {}

  getTournaments(): Observable<TournamentDTO[]> {
    return this.http
      .get<TournamentDTO[]>(API_ENDPOINTS.tournaments, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  syncWorldCupData(): Observable<{ message: string }> {
    return this.http
      .post<{ message: string }>(`${API_ENDPOINTS.worldcup}/sync`, {}, { context: this.silentContext() })
      .pipe(catchError(() => of({ message: 'Sync request skipped' })));
  }

  getCurrentTournament(): Observable<TournamentDTO> {
    return this.http
      .get<TournamentDTO>(`${API_ENDPOINTS.tournaments}/current`, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of({
            id: 1,
            name: 'FIFA World Cup',
            year: 2026,
            startDate: '2026-06-11',
            endDate: '2026-07-19',
            status: 'ACTIVE',
          }),
        ),
      );
  }

  getTournamentById(id: number): Observable<TournamentDTO> {
    return this.http
      .get<TournamentDTO>(`${API_ENDPOINTS.tournaments}/${id}`, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of({
            id,
            name: 'FIFA World Cup',
            year: 2026,
            startDate: '2026-06-11',
            endDate: '2026-07-19',
            status: 'ACTIVE',
          }),
        ),
      );
  }

  getTeams(name?: string): Observable<TeamDTO[]> {
    const url = name ? `${API_ENDPOINTS.teams}?name=${name}` : API_ENDPOINTS.teams;
    return this.http
      .get<TeamDTO[]>(url, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getStadiums(): Observable<StadiumDTO[]> {
    return this.http
      .get<StadiumDTO[]>(`${API_ENDPOINTS.worldcup}/stadiums`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getStadiumById(id: number): Observable<StadiumDTO> {
    return this.http
      .get<StadiumDTO>(`${API_ENDPOINTS.worldcup}/stadiums/${id}`, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of({
            id,
            name: 'Unknown Stadium',
          }),
        ),
      );
  }

  getTeamById(id: number): Observable<TeamDTO> {
    return this.http
      .get<TeamDTO>(`${API_ENDPOINTS.teams}/${id}`, { context: this.silentContext() })
      .pipe(catchError(() => of({ id, name: 'Unknown', code: 'UNK' })));
  }

  getPlayersByTeam(teamId: number): Observable<PlayerDTO[]> {
    return this.http
      .get<
        PlayerDTO[]
      >(`${API_ENDPOINTS.players}/team/${teamId}`, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getPlayerById(id: number): Observable<PlayerDTO> {
    return this.http
      .get<PlayerDTO>(`${API_ENDPOINTS.players}/${id}`, { context: this.silentContext() })
      .pipe(
        catchError(() => of({ id, name: 'Unknown', position: 'Unknown', number: 0, teamId: 0 })),
      );
  }

  getMatchesByDate(date: string): Observable<MatchDTO[]> {
    return this.cached(`matches:${date}`, CACHE_TTL.scheduledMatches, () =>
      this.http
        .get<
          MatchDTO[]
        >(`${API_ENDPOINTS.worldcup}/matches/date/${date}`, { context: this.silentContext() })
        .pipe(catchError(() => of([]))),
    );
  }

  getLiveMatches(): Observable<MatchDTO[]> {
    return this.cached('worldcup:matches:live', CACHE_TTL.liveMatches, () =>
      this.http
        .get<MatchDTO[]>(`${API_ENDPOINTS.worldcup}/matches/live`, { context: this.silentContext() })
        .pipe(catchError(() => of([]))),
    );
  }

  getMatchById(id: number): Observable<MatchDTO> {
    return this.http
      .get<MatchDTO>(`${API_ENDPOINTS.worldcup}/matches/${id}`, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of({
            id,
            stadiumId: 0,
            stadiumName: 'Unknown Stadium',
            homeTeamId: 0,
            awayTeamId: 0,
            homeTeam: 'Unknown',
            awayTeam: 'Unknown',
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
