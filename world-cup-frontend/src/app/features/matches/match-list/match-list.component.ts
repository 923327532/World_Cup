import { Component, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatchApiService } from '../../../services/api/match-api.service';
import { combineLatest, map, startWith } from 'rxjs';
import { Match } from '../../../models/match.model';

interface MatchGroup {
  date: string;
  matches: Match[];
}

@Component({
  selector: 'app-match-list',
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.scss'],
  standalone: false
})
export class MatchListComponent {
  private readonly matchApiService = inject(MatchApiService);

  readonly dateFilter = new FormControl('', { nonNullable: true });

  readonly groupedMatches$ = combineLatest([
    this.matchApiService.getAllMatches(),
    this.dateFilter.valueChanges.pipe(startWith(this.dateFilter.value)),
  ]).pipe(
    map(([matches, dateFilter]) => this.buildGroups(matches, dateFilter)),
  );

  clearFilter(): void {
    this.dateFilter.setValue('');
  }

  private buildGroups(matches: Match[], dateFilter: string): MatchGroup[] {
    const normalizedFilter = (dateFilter || '').trim();

    const filteredMatches = matches
      .filter((match) => {
        if (!normalizedFilter) {
          return true;
        }
        return this.toDateOnly(match.kickoffTime) === normalizedFilter;
      })
      .sort((a, b) => (a.kickoffTime || '').localeCompare(b.kickoffTime || ''));

    const grouped = new Map<string, Match[]>();

    for (const match of filteredMatches) {
      const matchDate = this.toDateOnly(match.kickoffTime);
      if (!grouped.has(matchDate)) {
        grouped.set(matchDate, []);
      }
      grouped.get(matchDate)?.push(match);
    }

    return Array.from(grouped.entries())
      .sort(([dateA], [dateB]) => dateA.localeCompare(dateB))
      .map(([date, items]) => ({ date, matches: items }));
  }

  private toDateOnly(value: string | undefined): string {
    if (!value) {
      return 'Sin fecha';
    }
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value.slice(0, 10) || 'Sin fecha';
    }
    return parsed.toISOString().slice(0, 10);
  }
}
