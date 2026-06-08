import { Component, inject } from '@angular/core';
import { of, switchMap } from 'rxjs';
import { ScoringApiService } from '../../../services/api/scoring-api.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-achievements',
  templateUrl: './achievements.component.html',
  styleUrls: ['./achievements.component.scss'],
  standalone: false
})
export class AchievementsComponent {
  private readonly scoringApiService = inject(ScoringApiService);
  private readonly authService = inject(AuthService);
  readonly user$ = this.authService.user$;
  readonly score$ = this.user$.pipe(
    switchMap((user) => user ? this.scoringApiService.getUserScore(user.userId) : of({ userId: 0, totalPoints: 0 }))
  );
  readonly history$ = this.user$.pipe(
    switchMap((user) => user ? this.scoringApiService.getUserHistory(user.userId) : of([]))
  );

}
