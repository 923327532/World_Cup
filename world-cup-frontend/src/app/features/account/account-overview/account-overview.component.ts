import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-account-overview',
  templateUrl: './account-overview.component.html',
  styleUrls: ['./account-overview.component.scss'],
  standalone: false,
})
export class AccountOverviewComponent {
  private readonly authService = inject(AuthService);

  readonly user$ = this.authService.user$;
}
