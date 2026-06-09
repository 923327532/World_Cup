import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { OrganizationRoutingModule } from './organization-routing.module';
import { OrganizationOverviewComponent } from './organization-overview/organization-overview.component';

@NgModule({
  declarations: [OrganizationOverviewComponent],
  imports: [SharedModule, OrganizationRoutingModule],
})
export class OrganizationModule {}
