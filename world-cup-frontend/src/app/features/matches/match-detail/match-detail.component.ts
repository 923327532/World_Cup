import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { map, switchMap } from 'rxjs';
import { MatchApiService } from '../../../services/api/match-api.service';

@Component({
  selector: 'app-match-detail',
  templateUrl: './match-detail.component.html',
  styleUrls: ['./match-detail.component.scss'],
  standalone: false
})
export class MatchDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly matchApiService = inject(MatchApiService);

  readonly match$ = this.route.paramMap.pipe(
    map((params) => Number(params.get('id') || 1)),
    switchMap((id) => this.matchApiService.getMatch(id))
  );
}
