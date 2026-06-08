import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GlobalRankingComponent } from './global-ranking/global-ranking.component';
import { CampusRankingComponent } from './campus-ranking/campus-ranking.component';
import { CareerRankingComponent } from './career-ranking/career-ranking.component';
import { DepartmentRankingComponent } from './department-ranking/department-ranking.component';

const routes: Routes = [
  { path: '', component: GlobalRankingComponent },
  { path: 'campus', component: CampusRankingComponent },
  { path: 'career', component: CareerRankingComponent },
  { path: 'department', component: DepartmentRankingComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RankingsRoutingModule {}
