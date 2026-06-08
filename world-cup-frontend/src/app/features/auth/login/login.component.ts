import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: false
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  readonly form = this.fb.group({
    email: ['capitan@tecsup.edu.pe', [Validators.required, Validators.email]],
    password: ['mundial2026', Validators.required]
  });

  submit(): void {
    if (this.form.invalid) return;
    const { email, password } = this.form.getRawValue();
    this.authService.login(email!, password!).subscribe(() => {
      this.notificationService.success('Sesion iniciada');
      this.router.navigate(['/dashboard']);
    });
  }
}
