import { Component, inject } from '@angular/core';
import { GamificationApiService } from '../../../services/api/gamification-api.service';

@Component({
  selector: 'app-rewards',
  templateUrl: './rewards.component.html',
  styleUrls: ['./rewards.component.scss'],
  standalone: false
})
export class RewardsComponent {
  private readonly gamificationApiService = inject(GamificationApiService);
  readonly rewards$ = this.gamificationApiService.getRewards();
}
