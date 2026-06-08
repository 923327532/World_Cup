import { Component, Input } from '@angular/core';
import { Match } from '../../../models/match.model';

@Component({
  selector: 'app-match-card',
  templateUrl: './match-card.component.html',
  styleUrls: ['./match-card.component.scss'],
  standalone: false
})
export class MatchCardComponent {
  @Input({ required: true }) match!: Match;
}
