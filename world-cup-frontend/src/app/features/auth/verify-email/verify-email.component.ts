import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss'],
  standalone: false
})
export class VerifyEmailComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    token: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
  });

  submit(): void {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    this.authService.verifyEmail(value.email!, value.token!).subscribe();
  }
}
