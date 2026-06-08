import { Component, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { startWith, switchMap } from 'rxjs';
import { RankingApiService } from '../../../services/api/ranking-api.service';
import { OrganizationApiService } from '../../../services/api/organization-api.service';

@Component({
  selector: 'app-career-ranking',
  templateUrl: './career-ranking.component.html',
  styleUrls: ['./career-ranking.component.scss'],
  standalone: false
})
export class CareerRankingComponent {
  private readonly rankingApiService = inject(RankingApiService);
  private readonly organizationApiService = inject(OrganizationApiService);
  readonly careers$ = this.organizationApiService.getCareersByDepartment(1);
  readonly career = new FormControl(1, { nonNullable: true });
  readonly ranking$ = this.career.valueChanges.pipe(
    startWith(this.career.value),
    switchMap((careerId) => this.rankingApiService.getCareerRanking(careerId))
  );
}
