import { Component, inject } from '@angular/core';
import { RankingApiService } from '../../../../services/api/ranking-api.service';

@Component({
  selector: 'app-top-ranking',
  templateUrl: './top-ranking.component.html',
  styleUrls: ['./top-ranking.component.scss'],
  standalone: false
})
export class TopRankingComponent {
  private readonly rankingApiService = inject(RankingApiService);
  readonly ranking$ = this.rankingApiService.getGlobalRanking();
}
