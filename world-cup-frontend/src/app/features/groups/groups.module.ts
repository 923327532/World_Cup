import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { GroupsRoutingModule } from './groups-routing.module';
import { GroupsCenterComponent } from './groups-center/groups-center.component';

@NgModule({
  declarations: [GroupsCenterComponent],
  imports: [SharedModule, ReactiveFormsModule, GroupsRoutingModule],
})
export class GroupsModule {}
