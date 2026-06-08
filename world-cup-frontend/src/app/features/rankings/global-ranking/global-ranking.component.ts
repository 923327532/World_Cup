import { Component, inject } from '@angular/core';
import { RankingApiService } from '../../../services/api/ranking-api.service';

@Component({
  selector: 'app-global-ranking',
  templateUrl: './global-ranking.component.html',
  styleUrls: ['./global-ranking.component.scss'],
  standalone: false
})
export class GlobalRankingComponent {
  private readonly rankingApiService = inject(RankingApiService);
  readonly ranking$ = this.rankingApiService.getGlobalRanking();
  readonly displayedColumns = ['position', 'userId', 'points'];
}
