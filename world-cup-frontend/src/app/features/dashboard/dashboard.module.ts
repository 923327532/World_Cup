import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardPageComponent } from './dashboard-page/dashboard-page.component';
import { ActiveMatchesComponent } from './widgets/active-matches/active-matches.component';
import { TopRankingComponent } from './widgets/top-ranking/top-ranking.component';
import { LiveUsersComponent } from './widgets/live-users/live-users.component';

@NgModule({
  declarations: [DashboardPageComponent, ActiveMatchesComponent, TopRankingComponent, LiveUsersComponent],
  imports: [SharedModule, DashboardRoutingModule]
})
export class DashboardModule {}
