import { NgModule } from '@angular/core';
import { SocialRoutingModule } from './social-routing.module';
import { SocialComponentsModule } from './social-components.module';

@NgModule({
  imports: [SocialComponentsModule, SocialRoutingModule],
  exports: [SocialComponentsModule],
})
export class SocialModule {}
