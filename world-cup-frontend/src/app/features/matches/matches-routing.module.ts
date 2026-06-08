import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MatchListComponent } from './match-list/match-list.component';
import { LiveMatchComponent } from './live-match/live-match.component';
import { MatchDetailComponent } from './match-detail/match-detail.component';

const routes: Routes = [
  { path: '', component: MatchListComponent },
  { path: 'live', component: LiveMatchComponent },
  { path: ':id', component: MatchDetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MatchesRoutingModule {}
