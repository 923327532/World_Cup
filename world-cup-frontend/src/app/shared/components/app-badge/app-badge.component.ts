import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-badge',
  templateUrl: './app-badge.component.html',
  styleUrls: ['./app-badge.component.scss'],
  standalone: false
})
export class AppBadgeComponent {
  @Input() label = '';
  @Input() tone: 'neutral' | 'success' | 'live' = 'neutral';
}
