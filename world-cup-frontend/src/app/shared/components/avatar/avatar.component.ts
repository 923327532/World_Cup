import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss'],
  standalone: false
})
export class AvatarComponent {
  @Input() name = '';
  @Input() imageUrl?: string;

  get initials(): string {
    return this.name.split(' ').map((part) => part[0]).join('').slice(0, 2).toUpperCase() || 'WC';
  }
}
