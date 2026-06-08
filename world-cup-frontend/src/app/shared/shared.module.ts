import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialModule } from './material/material.module';
import { LoadingComponent } from './components/loading/loading.component';
import { AvatarComponent } from './components/avatar/avatar.component';
import { AppBadgeComponent } from './components/app-badge/app-badge.component';
import { MatchCardComponent } from './components/match-card/match-card.component';
import { AppDatePipe } from './pipes/date.pipe';
import { PointsPipe } from './pipes/points.pipe';
import { ClickOutsideDirective } from './directives/click-outside.directive';

const DECLARATIONS = [
  LoadingComponent,
  AvatarComponent,
  AppBadgeComponent,
  MatchCardComponent,
  AppDatePipe,
  PointsPipe,
  ClickOutsideDirective
];

@NgModule({
  declarations: DECLARATIONS,
  imports: [CommonModule, MaterialModule],
  exports: [CommonModule, MaterialModule, ...DECLARATIONS]
})
export class SharedModule {}
