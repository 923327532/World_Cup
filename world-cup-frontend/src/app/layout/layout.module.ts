import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
import { NavbarComponent } from './navbar/navbar.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { FooterComponent } from './footer/footer.component';
import { DashboardLayoutComponent } from './dashboard-layout/dashboard-layout.component';
import { PublicLayoutComponent } from './public-layout/public-layout.component';

@NgModule({
  declarations: [
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    DashboardLayoutComponent,
    PublicLayoutComponent
  ],
  imports: [RouterModule, SharedModule],
  exports: [DashboardLayoutComponent, PublicLayoutComponent]
})
export class LayoutModule {}
