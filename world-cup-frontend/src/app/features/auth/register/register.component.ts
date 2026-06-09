import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: false
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    role: ['STUDENT', Validators.required],
    studentCode: ['']
  });

  errorMessage: string | null = null;
  isLoading = false;
  hidePassword = true;

  submit(): void {
    if (this.form.invalid) return;

    this.errorMessage = null;
    this.isLoading = true;

    const value = this.form.getRawValue();
    this.authService.register(
      value.firstName!,
      value.lastName!,
      value.email!,
      value.password!,
      value.role as 'STUDENT' | 'TEACHER',
      value.studentCode || undefined
    ).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success('Registro exitoso. Revisa tu correo para verificar la cuenta.');
        this.router.navigate(['/auth/verify-email']);
      },
      error: (error: Error) => {
        this.isLoading = false;
        this.errorMessage = error.message || 'No se pudo completar el registro. Intenta de nuevo.';
        this.notificationService.error(this.errorMessage);
      }
    });
  }
}
