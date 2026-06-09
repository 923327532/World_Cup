import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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
  private readonly route = inject(ActivatedRoute);
  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  errorMessage: string | null = null;
  isLoading = false;
  hidePassword = true;

  constructor() {
    const queryMap = this.route.snapshot.queryParamMap;
    const email = queryMap.get('email');

    if (email) {
      this.form.patchValue({ email });
    }

    if (queryMap.get('registered') === '1') {
      this.notificationService.info('Cuenta creada. Inicia sesion para continuar al dashboard.');
    }

    if (queryMap.get('exists') === '1') {
      this.notificationService.info('Ese correo ya estaba registrado. Inicia sesion con tu cuenta.');
    }
  }

  submit(): void {
    if (this.form.invalid) return;

    this.errorMessage = null;
    this.isLoading = true;

    const { email, password } = this.form.getRawValue();
    this.authService.login(email!, password!).subscribe({
      next: () => {
        this.isLoading = false;
        this.notificationService.success('Sesión iniciada');
        const user = this.authService.getCurrentUser();
        const target = user?.role === 'ADMIN' ? '/admin' : '/app/dashboard';
        this.router.navigate([target]);
      },
      error: (error: Error) => {
        this.isLoading = false;
        this.errorMessage = error.message || 'No se pudo iniciar sesión. Intenta de nuevo.';
        this.notificationService.error(this.errorMessage);
      }
    });
  }
}
