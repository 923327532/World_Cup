import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { MatchesRoutingModule } from './matches-routing.module';
import { MatchListComponent } from './match-list/match-list.component';
import { LiveMatchComponent } from './live-match/live-match.component';
import { MatchDetailComponent } from './match-detail/match-detail.component';
import { SocialComponentsModule } from '../social/social-components.module';

@NgModule({
  declarations: [MatchListComponent, LiveMatchComponent, MatchDetailComponent],
  imports: [SharedModule, ReactiveFormsModule, SocialComponentsModule, MatchesRoutingModule]
})
export class MatchesModule {}
