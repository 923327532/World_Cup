import { Component, inject } from '@angular/core';
import { GamificationApiService } from '../../../services/api/gamification-api.service';

@Component({
  selector: 'app-badges',
  templateUrl: './badges.component.html',
  styleUrls: ['./badges.component.scss'],
  standalone: false
})
export class BadgesComponent {
  private readonly gamificationApiService = inject(GamificationApiService);
  readonly badges$ = this.gamificationApiService.getBadges();
  readonly userBadges$ = this.gamificationApiService.getUserBadges();
}
