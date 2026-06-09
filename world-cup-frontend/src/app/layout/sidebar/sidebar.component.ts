import { Component } from '@angular/core';

interface NavItem {
  icon: string;
  label: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  standalone: false,
})
export class SidebarComponent {
  readonly items: NavItem[] = [
    { icon: 'dashboard', label: 'Dashboard', route: '/dashboard' },
    { icon: 'event', label: 'Partidos', route: '/matches' },
    { icon: 'edit_note', label: 'Predicciones', route: '/predictions' },
    { icon: 'leaderboard', label: 'Rankings', route: '/rankings' },
    { icon: 'forum', label: 'Social', route: '/social' },
    { icon: 'workspace_premium', label: 'Logros', route: '/gamification' },
    { icon: 'admin_panel_settings', label: 'Admin', route: '/admin' },
  ];
}
