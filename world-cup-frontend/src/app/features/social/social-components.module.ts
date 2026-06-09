import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { LiveChatComponent } from './live-chat/live-chat.component';
import { ReactionsComponent } from './reactions/reactions.component';

@NgModule({
  declarations: [LiveChatComponent, ReactionsComponent],
  imports: [SharedModule, ReactiveFormsModule],
  exports: [LiveChatComponent, ReactionsComponent],
})
export class SocialComponentsModule {}
