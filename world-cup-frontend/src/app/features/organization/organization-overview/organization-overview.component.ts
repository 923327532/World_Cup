import { Component, inject } from '@angular/core';
import { combineLatest, of, switchMap } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { OrganizationApiService } from '../../../services/api/organization-api.service';

@Component({
  selector: 'app-organization-overview',
  templateUrl: './organization-overview.component.html',
  styleUrls: ['./organization-overview.component.scss'],
  standalone: false,
})
export class OrganizationOverviewComponent {
  private readonly authService = inject(AuthService);
  private readonly organizationApiService = inject(OrganizationApiService);

  readonly user$ = this.authService.user$;
  readonly campuses$ = this.organizationApiService.getCampuses();
  readonly departments$ = this.organizationApiService.getDepartments();
  readonly careers$ = this.organizationApiService.getCareers();
  readonly avatars$ = this.organizationApiService.getAvatars();

  readonly profile$ = this.user$.pipe(
    switchMap((user) => {
      if (!user) return of(null);
      if (user.role === 'TEACHER') {
        return this.organizationApiService.getTeacherProfileByUser(user.userId);
      }
      return this.organizationApiService.getStudentProfileByUser(user.userId);
    }),
  );

  readonly summary$ = combineLatest([
    this.campuses$,
    this.departments$,
    this.careers$,
    this.avatars$,
  ]);
}
