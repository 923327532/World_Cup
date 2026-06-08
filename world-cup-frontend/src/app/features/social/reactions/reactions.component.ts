import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-reactions',
  templateUrl: './reactions.component.html',
  styleUrls: ['./reactions.component.scss'],
  standalone: false
})
export class ReactionsComponent {
  @Input() reactions: Record<string, number> = {};
  @Output() reaction = new EventEmitter<string>();

  readonly options = ['👍', '🔥', '👏'];

  entries(): Array<[string, number]> {
    return Object.entries(this.reactions);
  }
}
