import { Component, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { startWith, switchMap } from 'rxjs';
import { RankingApiService } from '../../../services/api/ranking-api.service';
import { OrganizationApiService } from '../../../services/api/organization-api.service';

@Component({
  selector: 'app-department-ranking',
  templateUrl: './department-ranking.component.html',
  styleUrls: ['./department-ranking.component.scss'],
  standalone: false
})
export class DepartmentRankingComponent {
  private readonly rankingApiService = inject(RankingApiService);
  private readonly organizationApiService = inject(OrganizationApiService);
  readonly departments$ = this.organizationApiService.getDepartments();
  readonly department = new FormControl(1, { nonNullable: true });
  readonly ranking$ = this.department.valueChanges.pipe(
    startWith(this.department.value),
    switchMap((departmentId) => this.rankingApiService.getDepartmentRanking(departmentId))
  );
}
