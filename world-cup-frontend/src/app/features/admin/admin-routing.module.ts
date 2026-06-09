import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { AdminMatchesComponent } from './admin-matches/admin-matches.component';
import { AdminAuditComponent } from './admin-audit/admin-audit.component';

const routes: Routes = [
  { path: '', component: AdminDashboardComponent },
  { path: 'matches', component: AdminMatchesComponent },
  { path: 'audit', component: AdminAuditComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule {}
