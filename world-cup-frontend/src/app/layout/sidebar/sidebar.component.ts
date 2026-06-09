import { Component, inject } from '@angular/core';
import { map } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

interface NavItem {
  icon: string;
  label: string;
  route: string;
  roles?: Array<'STUDENT' | 'TEACHER' | 'ADMIN'>;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  standalone: false,
})
export class SidebarComponent {
  private readonly authService = inject(AuthService);

  readonly items: NavItem[] = [
    { icon: 'dashboard', label: 'Dashboard', route: '/app/dashboard' },
    { icon: 'event', label: 'Partidos', route: '/app/matches' },
    { icon: 'edit_note', label: 'Predicciones', route: '/app/predictions', roles: ['STUDENT', 'TEACHER'] },
    { icon: 'leaderboard', label: 'Rankings', route: '/app/rankings' },
    { icon: 'forum', label: 'Social', route: '/app/social', roles: ['STUDENT', 'TEACHER'] },
    { icon: 'workspace_premium', label: 'Logros', route: '/app/gamification', roles: ['STUDENT', 'TEACHER'] },
    { icon: 'groups', label: 'Grupos', route: '/app/groups', roles: ['STUDENT', 'TEACHER'] },
    { icon: 'notifications', label: 'Notificaciones', route: '/app/notifications', roles: ['STUDENT', 'TEACHER', 'ADMIN'] },
    { icon: 'badge', label: 'Cuenta', route: '/app/account', roles: ['STUDENT', 'TEACHER', 'ADMIN'] },
    { icon: 'admin_panel_settings', label: 'Admin', route: '/admin', roles: ['ADMIN'] },
  ];

  readonly items$ = this.authService.user$.pipe(
    map((user) => {
      if (!user) return this.items;
      return this.items.filter((item) => !item.roles || item.roles.includes(user.role));
    })
  );
}
