import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationApiService } from '../../services/api/notification-api.service';
import { map, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: false
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);
  private readonly notificationApiService = inject(NotificationApiService);
  private readonly router = inject(Router);
  readonly user$ = this.authService.user$;
  readonly unreadCount$ = this.user$.pipe(
    switchMap((user) => user ? this.notificationApiService.getUnreadNotifications(user.userId).pipe(map((items) => items.length)) : of(0))
  );

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
