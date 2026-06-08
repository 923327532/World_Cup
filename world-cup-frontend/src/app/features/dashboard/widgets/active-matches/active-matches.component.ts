import { Component } from '@angular/core';
import { Observable, interval, startWith, switchMap } from 'rxjs';
import { MatchApiService } from '../../../../services/api/match-api.service';
import { Match } from '../../../../models/match.model';

@Component({
  selector: 'app-active-matches',
  templateUrl: './active-matches.component.html',
  styleUrls: ['./active-matches.component.scss'],
  standalone: false
})
export class ActiveMatchesComponent {
  readonly matches$: Observable<Match[]> = interval(15000).pipe(
    startWith(0),
    switchMap(() => this.matchApiService.getLiveMatches())
  );

  constructor(private matchApiService: MatchApiService) {}
}
