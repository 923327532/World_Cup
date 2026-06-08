import { Component, inject } from '@angular/core';
import { of, switchMap } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { ScoringApiService } from '../../../services/api/scoring-api.service';
import { WorldcupApiService } from '../../../services/api/worldcup-api.service';

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss'],
  standalone: false
})
export class DashboardPageComponent {
  private readonly worldcupApiService = inject(WorldcupApiService);
  private readonly scoringApiService = inject(ScoringApiService);
  private readonly authService = inject(AuthService);

  readonly tournament$ = this.worldcupApiService.getCurrentTournament();
  readonly score$ = this.authService.user$.pipe(
    switchMap((user) => (user ? this.scoringApiService.getUserScore(user.userId) : of({ userId: 0, totalPoints: 0 })))
  );
}
