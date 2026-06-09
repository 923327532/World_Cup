import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { AccountRoutingModule } from './account-routing.module';
import { AccountOverviewComponent } from './account-overview/account-overview.component';

@NgModule({
  declarations: [AccountOverviewComponent],
  imports: [SharedModule, AccountRoutingModule],
})
export class AccountModule {}
