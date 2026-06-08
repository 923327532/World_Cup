import { Component, inject } from '@angular/core';
import { Observable, combineLatest, map, startWith } from 'rxjs';
import { Match } from '../../../models/match.model';
import { MatchApiService } from '../../../services/api/match-api.service';
import { WebsocketService } from '../../../core/services/websocket.service';

@Component({
  selector: 'app-live-match',
  templateUrl: './live-match.component.html',
  styleUrls: ['./live-match.component.scss'],
  standalone: false
})
export class LiveMatchComponent {
  private readonly matchApiService = inject(MatchApiService);
  private readonly websocketService = inject(WebsocketService);
  readonly match$: Observable<Match> = combineLatest([
    this.matchApiService.getMatch(1),
    this.websocketService.subscribeToLiveScores().pipe(startWith(null))
  ]).pipe(
    map(([match, update]) => update ? { ...match, homeScore: update.homeScore, awayScore: update.awayScore } : match)
  );

}
