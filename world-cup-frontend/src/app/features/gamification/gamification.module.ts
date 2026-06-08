import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { AchievementsComponent } from './achievements/achievements.component';
import { BadgesComponent } from './badges/badges.component';
import { GamificationRoutingModule } from './gamification-routing.module';
import { RewardsComponent } from './rewards/rewards.component';

@NgModule({
  declarations: [BadgesComponent, AchievementsComponent, RewardsComponent],
  imports: [SharedModule, GamificationRoutingModule]
})
export class GamificationModule {}
