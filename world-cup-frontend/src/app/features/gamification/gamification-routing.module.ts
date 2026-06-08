import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BadgesComponent } from './badges/badges.component';
import { AchievementsComponent } from './achievements/achievements.component';
import { RewardsComponent } from './rewards/rewards.component';

const routes: Routes = [
  { path: '', component: BadgesComponent },
  { path: 'achievements', component: AchievementsComponent },
  { path: 'rewards', component: RewardsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GamificationRoutingModule {}
