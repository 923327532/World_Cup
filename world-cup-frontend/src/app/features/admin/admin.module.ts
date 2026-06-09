import { NgModule } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { AdminRoutingModule } from './admin-routing.module';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { AdminMatchesComponent } from './admin-matches/admin-matches.component';
import { AdminAuditComponent } from './admin-audit/admin-audit.component';

@NgModule({
  declarations: [AdminDashboardComponent, AdminMatchesComponent, AdminAuditComponent],
  imports: [SharedModule, ReactiveFormsModule, FormsModule, AdminRoutingModule],
})
export class AdminModule {}
