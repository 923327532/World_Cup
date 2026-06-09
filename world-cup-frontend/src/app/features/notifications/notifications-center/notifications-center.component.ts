import { Component, inject } from '@angular/core';
import { of, switchMap } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationApiService, NotificationDTO } from '../../../services/api/notification-api.service';

@Component({
  selector: 'app-notifications-center',
  templateUrl: './notifications-center.component.html',
  styleUrls: ['./notifications-center.component.scss'],
  standalone: false,
})
export class NotificationsCenterComponent {
  private readonly authService = inject(AuthService);
  private readonly notificationApiService = inject(NotificationApiService);

  readonly notifications$ = this.authService.user$.pipe(
    switchMap((user) => (user ? this.notificationApiService.getUserNotifications(user.userId) : of([]))),
  );

  markAsRead(notification: NotificationDTO): void {
    if (notification.isRead) return;
    this.notificationApiService.markAsRead(notification.id).subscribe();
    notification.isRead = true;
  }
}
