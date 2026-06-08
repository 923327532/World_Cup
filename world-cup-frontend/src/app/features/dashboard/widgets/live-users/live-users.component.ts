import { Component } from '@angular/core';
import { interval, map, startWith } from 'rxjs';

@Component({
  selector: 'app-live-users',
  templateUrl: './live-users.component.html',
  styleUrls: ['./live-users.component.scss'],
  standalone: false
})
export class LiveUsersComponent {
  readonly liveUsers$ = interval(5000).pipe(
    startWith(0),
    map((tick) => 124 + (tick % 12))
  );
}
