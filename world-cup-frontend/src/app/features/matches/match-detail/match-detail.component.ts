import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, map, switchMap } from 'rxjs';
import { MatchApiService } from '../../../services/api/match-api.service';
import { WorldcupApiService } from '../../../services/api/worldcup-api.service';

@Component({
  selector: 'app-match-detail',
  templateUrl: './match-detail.component.html',
  styleUrls: ['./match-detail.component.scss'],
  standalone: false
})
export class MatchDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly matchApiService = inject(MatchApiService);
  private readonly worldcupApiService = inject(WorldcupApiService);

  readonly viewModel$ = combineLatest([
    this.route.paramMap.pipe(
      map((params) => Number(params.get('id') || 1)),
      switchMap((id) => this.matchApiService.getMatch(id)),
    ),
    this.worldcupApiService.getStadiums(),
  ]).pipe(
    map(([match, stadiums]) => ({
      match,
      stadium: stadiums.find((item) => item.id === match.stadiumId) ?? null,
    })),
  );
}
