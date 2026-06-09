import { Component, inject } from '@angular/core';
import { interval, map, startWith, switchMap } from 'rxjs';
import { RankingApiService } from '../../../../services/api/ranking-api.service';

@Component({
  selector: 'app-live-users',
  templateUrl: './live-users.component.html',
  styleUrls: ['./live-users.component.scss'],
  standalone: false
})
export class LiveUsersComponent {
  private readonly rankingApiService = inject(RankingApiService);

  readonly liveUsers$ = interval(30000).pipe(
    startWith(0),
    switchMap(() => this.rankingApiService.getGlobalRanking(200)),
    map((ranking) => ranking.length)
  );
}
