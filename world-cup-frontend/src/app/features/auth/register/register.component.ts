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
    firstName: ['', [Validators.required, Validators.pattern(/.*\S.*/)]],
    lastName: ['', [Validators.required, Validators.pattern(/.*\S.*/)]],
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
    const firstName = (value.firstName || '').trim();
    const lastName = (value.lastName || '').trim();
    const email = (value.email || '').trim();
    const studentCode = (value.studentCode || '').trim() || undefined;
    const role = value.role as 'STUDENT' | 'TEACHER';

    if (!firstName || !lastName || !email) {
      this.isLoading = false;
      this.errorMessage = 'Completa nombres, apellidos y correo con valores validos.';
      return;
    }

    if (role === 'STUDENT' && !studentCode) {
      this.isLoading = false;
      this.errorMessage = 'El codigo estudiantil es obligatorio para estudiantes.';
      return;
    }

    this.authService.register(
      firstName,
      lastName,
      email,
      value.password!,
      role,
      studentCode
    ).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success('Registro exitoso. Ahora inicia sesión para entrar al dashboard.');
        this.router.navigate(['/auth/login'], { queryParams: { email, registered: '1' } });
      },
      error: (error: Error) => {
        this.isLoading = false;
        const rawMessage = error.message || 'No se pudo completar el registro. Intenta de nuevo.';
        this.errorMessage = rawMessage;
        if (/registrado|already/i.test(rawMessage)) {
          this.errorMessage = 'Ese correo ya esta registrado. Inicia sesion para entrar al dashboard.';
          this.router.navigate(['/auth/login'], { queryParams: { email, exists: '1' } });
        } else if (/acceso denegado|forbidden/i.test(rawMessage)) {
          this.errorMessage = 'Ese correo posiblemente ya existe o aun no esta verificado. Inicia sesion o verifica tu correo.';
          this.router.navigate(['/auth/login'], { queryParams: { email, exists: '1' } });
        }
        this.notificationService.error(this.errorMessage);
      }
    });
  }
}
