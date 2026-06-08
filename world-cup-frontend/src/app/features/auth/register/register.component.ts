import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: false
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    role: ['STUDENT', Validators.required],
    studentCode: ['']
  });

  submit(): void {
    if (this.form.invalid) return;
    const value = this.form.getRawValue();
    this.authService.register(
      value.firstName!,
      value.lastName!,
      value.email!,
      value.password!,
      value.role as 'STUDENT' | 'TEACHER',
      value.studentCode || undefined
    ).subscribe(() => {
      this.router.navigate(['/auth/verify-email']);
    });
  }
}
