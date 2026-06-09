import { Component, OnInit, inject } from '@angular/core';
import { combineLatest, map, of, switchMap } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { ScoringApiService } from '../../../services/api/scoring-api.service';
import { WorldcupApiService } from '../../../services/api/worldcup-api.service';

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss'],
  standalone: false
})
export class DashboardPageComponent implements OnInit {
  private readonly worldcupApiService = inject(WorldcupApiService);
  private readonly scoringApiService = inject(ScoringApiService);
  private readonly authService = inject(AuthService);
  private readonly today = new Date().toISOString().slice(0, 10);

  readonly tournament$ = this.worldcupApiService.getCurrentTournament();
  readonly tournaments$ = this.worldcupApiService.getTournaments();
  readonly teams$ = this.worldcupApiService.getTeams();
  readonly stadiums$ = this.worldcupApiService.getStadiums();
  readonly liveMatches$ = this.worldcupApiService.getLiveMatches();
  readonly todayMatches$ = this.worldcupApiService.getMatchesByDate(this.today);
  readonly user$ = this.authService.user$;
  readonly role$ = this.user$.pipe(map((user) => user?.role || 'STUDENT'));
  readonly isAdmin$ = this.authService.user$.pipe(map((user) => user?.role === 'ADMIN'));
  readonly isTeacher$ = this.authService.user$.pipe(map((user) => user?.role === 'TEACHER'));
  readonly worldCupSummary$ = combineLatest([
    this.tournaments$,
    this.teams$,
    this.stadiums$,
    this.liveMatches$,
    this.todayMatches$,
  ]).pipe(
    map(([tournaments, teams, stadiums, liveMatches, todayMatches]) => ({
      tournaments: tournaments.length,
      teams: teams.length,
      stadiums: stadiums.length,
      liveMatches: liveMatches.length,
      todayMatches: todayMatches.length,
    }))
  );

  readonly featuredMatch$ = this.liveMatches$.pipe(map((matches) => matches[0] || null));
  readonly featuredMatchDetail$ = this.featuredMatch$.pipe(
    switchMap((match) => (match ? this.worldcupApiService.getMatchById(match.id) : of(null)))
  );
  readonly featuredStadium$ = this.featuredMatchDetail$.pipe(
    switchMap((match) => (match?.stadiumId ? this.worldcupApiService.getStadiumById(match.stadiumId) : of(null)))
  );
  readonly featuredHomeTeam$ = this.featuredMatchDetail$.pipe(
    switchMap((match) => (match?.homeTeamId ? this.worldcupApiService.getTeamById(match.homeTeamId) : of(null)))
  );
  readonly featuredAwayTeam$ = this.featuredMatchDetail$.pipe(
    switchMap((match) => (match?.awayTeamId ? this.worldcupApiService.getTeamById(match.awayTeamId) : of(null)))
  );
  readonly featuredPlayers$ = this.featuredMatchDetail$.pipe(
    switchMap((match) => (match?.homeTeamId ? this.worldcupApiService.getPlayersByTeam(match.homeTeamId) : of([])))
  );

  readonly score$ = this.authService.user$.pipe(
    switchMap((user) => (user ? this.scoringApiService.getUserScore(user.userId) : of({ userId: 0, totalPoints: 0 })))
  );

  ngOnInit(): void {
    if (this.authService.hasRole('ADMIN')) {
      this.worldcupApiService.syncWorldCupData().subscribe();
    }
  }
}
