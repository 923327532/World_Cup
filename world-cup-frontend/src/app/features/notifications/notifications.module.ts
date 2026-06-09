import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { NotificationsRoutingModule } from './notifications-routing.module';
import { NotificationsCenterComponent } from './notifications-center/notifications-center.component';

@NgModule({
  declarations: [NotificationsCenterComponent],
  imports: [SharedModule, NotificationsRoutingModule],
})
export class NotificationsModule {}
