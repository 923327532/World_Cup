import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GroupsCenterComponent } from './groups-center/groups-center.component';

const routes: Routes = [{ path: '', component: GroupsCenterComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GroupsRoutingModule {}
