import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { CampusRankingComponent } from './campus-ranking/campus-ranking.component';
import { CareerRankingComponent } from './career-ranking/career-ranking.component';
import { DepartmentRankingComponent } from './department-ranking/department-ranking.component';
import { GlobalRankingComponent } from './global-ranking/global-ranking.component';
import { RankingsRoutingModule } from './rankings-routing.module';

@NgModule({
  declarations: [GlobalRankingComponent, CampusRankingComponent, CareerRankingComponent, DepartmentRankingComponent],
  imports: [SharedModule, ReactiveFormsModule, RankingsRoutingModule]
})
export class RankingsModule {}
