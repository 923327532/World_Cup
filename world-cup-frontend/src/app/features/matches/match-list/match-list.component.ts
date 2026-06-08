import { Component, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatchApiService } from '../../../services/api/match-api.service';
import { startWith, switchMap } from 'rxjs';

@Component({
  selector: 'app-match-list',
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.scss'],
  standalone: false
})
export class MatchListComponent {
  private readonly matchApiService = inject(MatchApiService);
  readonly selectedDate = new FormControl(new Date().toISOString().slice(0, 10), { nonNullable: true });
  readonly matches$ = this.selectedDate.valueChanges.pipe(
    startWith(this.selectedDate.value),
    switchMap((date) => this.matchApiService.getMatchesByDate(date))
  );
}
