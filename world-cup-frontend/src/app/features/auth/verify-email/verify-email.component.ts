import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss'],
  standalone: false
})
export class VerifyEmailComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    token: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
  });

  isLoading = false;

  constructor() {
    const email = this.route.snapshot.queryParamMap.get('email');
    if (email) {
      this.form.patchValue({ email });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.isLoading = true;
    const value = this.form.getRawValue();
    this.authService.verifyEmail(value.email!, value.token!).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success('Correo verificado. Ya puedes entrar al dashboard.');
        if (this.authService.isAuthenticated()) {
          this.router.navigate(['/app/dashboard']);
          return;
        }
        this.router.navigate(['/auth/login'], { queryParams: { email: value.email } });
      },
      error: (error: Error) => {
        this.isLoading = false;
        this.notificationService.error(error.message || 'No se pudo verificar el correo.');
      }
    });
  }

  goToDashboardOrLogin(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/app/dashboard']);
      return;
    }
    this.router.navigate(['/auth/login'], { queryParams: { email: this.form.getRawValue().email || undefined } });
  }
}
