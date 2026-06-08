import { Component, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { startWith, switchMap } from 'rxjs';
import { RankingApiService } from '../../../services/api/ranking-api.service';
import { OrganizationApiService } from '../../../services/api/organization-api.service';

@Component({
  selector: 'app-campus-ranking',
  templateUrl: './campus-ranking.component.html',
  styleUrls: ['./campus-ranking.component.scss'],
  standalone: false
})
export class CampusRankingComponent {
  private readonly rankingApiService = inject(RankingApiService);
  private readonly organizationApiService = inject(OrganizationApiService);
  readonly campuses$ = this.organizationApiService.getCampuses();
  readonly campus = new FormControl(1, { nonNullable: true });
  readonly ranking$ = this.campus.valueChanges.pipe(
    startWith(this.campus.value),
    switchMap((campusId) => this.rankingApiService.getCampusRanking(campusId))
  );
}
