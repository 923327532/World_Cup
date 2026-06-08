import { Component, Input, OnDestroy, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import { SocialComment } from '../../../services/api/mock-data';
import { SocialApiService } from '../../../services/api/social-api.service';
import { WebsocketService } from '../../../core/services/websocket.service';

@Component({
  selector: 'app-live-chat',
  templateUrl: './live-chat.component.html',
  styleUrls: ['./live-chat.component.scss'],
  standalone: false
})
export class LiveChatComponent implements OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly socialApiService = inject(SocialApiService);
  private readonly websocketService = inject(WebsocketService);
  private chatSubscription?: Subscription;

  @Input() set matchId(value: number) {
    this.currentMatchId = value;
    this.comments$ = this.socialApiService.getComments(value);
    this.chatSubscription?.unsubscribe();
    this.chatSubscription = this.websocketService.subscribeToMatchChat(value).subscribe(() => {
      this.comments$ = this.socialApiService.getComments(value);
    });
  }

  currentMatchId = 1;
  comments$: Observable<SocialComment[]> = this.socialApiService.getComments(1);
  readonly form = this.fb.group({
    content: ['', [Validators.required, Validators.maxLength(180)]]
  });

  send(): void {
    const content = this.form.value.content?.trim();
    if (!content) return;
    this.socialApiService.addComment(this.currentMatchId, content).subscribe(() => {
      this.comments$ = this.socialApiService.getComments(this.currentMatchId);
      this.form.reset();
    });
  }

  react(commentId: number, emoji: string): void {
    this.socialApiService.react(commentId, emoji);
    this.comments$ = this.socialApiService.getComments(this.currentMatchId);
  }

  ngOnDestroy(): void {
    this.chatSubscription?.unsubscribe();
  }
}
