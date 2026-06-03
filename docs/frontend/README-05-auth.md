# README 05: Auth - Feature de Autenticación

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar el feature de autenticación que incluye login, registro y verificación de email.

---

## 1. ESTRUCTURA DEL FEATURE AUTH

```
features/auth
│
├── login
│   ├── login.component.ts
│   ├── login.component.html
│   └── login.component.scss
│
├── register
│   ├── register.component.ts
│   ├── register.component.html
│   └── register.component.scss
│
├── verify-email
│   ├── verify-email.component.ts
│   ├── verify-email.component.html
│   └── verify-email.component.scss
│
├── auth-routing.module.ts
└── auth.module.ts
```

---

## 2. LOGIN COMPONENT

### features/auth/login/login.component.ts
```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  
  loginForm: FormGroup;
  loading = false;
  hidePassword = true;
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }
  
  onSubmit(): void {
    if (this.loginForm.invalid) return;
    
    this.loading = true;
    
    const { email, password } = this.loginForm.value;
    
    this.authService.login(email, password).subscribe({
      next: () => {
        this.snackBar.open('Login successful!', 'Close', { duration: 3000 });
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Login failed. Please check your credentials.', 'Close', { duration: 5000 });
      }
    });
  }
  
  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }
}
```

### features/auth/login/login.component.html
```html
<div class="login-container">
  <mat-card class="login-card">
    <mat-card-header>
      <mat-card-title>Login</mat-card-title>
      <mat-card-subtitle>Enter your credentials to access the platform</mat-card-subtitle>
    </mat-card-header>
    
    <mat-card-content>
      <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" placeholder="student@tecsup.edu.pe" />
          <mat-error *ngIf="loginForm.get('email')?.hasError('required')">
            Email is required
          </mat-error>
          <mat-error *ngIf="loginForm.get('email')?.hasError('email')">
            Please enter a valid email
          </mat-error>
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Password</mat-label>
          <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" />
          <button mat-icon-button matSuffix (click)="togglePasswordVisibility()" type="button">
            <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
          </button>
          <mat-error *ngIf="loginForm.get('password')?.hasError('required')">
            Password is required
          </mat-error>
          <mat-error *ngIf="loginForm.get('password')?.hasError('minlength')">
            Password must be at least 8 characters
          </mat-error>
        </mat-form-field>
        
        <button mat-raised-button color="primary" type="submit" class="full-width" [disabled]="loading">
          <span *ngIf="!loading">Login</span>
          <span *ngIf="loading">Logging in...</span>
        </button>
      </form>
    </mat-card-content>
    
    <mat-card-actions>
      <p class="register-link">
        Don't have an account? <a routerLink="/auth/register">Register here</a>
      </p>
    </mat-card-actions>
  </mat-card>
</div>
```

### features/auth/login/login.component.scss
```scss
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 1rem;
  
  .login-card {
    max-width: 450px;
    width: 100%;
    
    .full-width {
      width: 100%;
      margin-bottom: 1rem;
    }
    
    .register-link {
      text-align: center;
      width: 100%;
      margin: 1rem 0 0;
      
      a {
        color: #1976d2;
        text-decoration: none;
        font-weight: 500;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
```

---

## 3. REGISTER COMPONENT

### features/auth/register/register.component.ts
```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  
  registerForm: FormGroup;
  loading = false;
  hidePassword = true;
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      role: ['STUDENT', [Validators.required]],
      studentCode: ['']
    }, { validators: this.passwordMatchValidator });
  }
  
  passwordMatchValidator(form: FormGroup): { [key: string]: boolean } | null {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    
    return null;
  }
  
  onSubmit(): void {
    if (this.registerForm.invalid) return;
    
    this.loading = true;
    
    const { email, password, firstName, lastName, role, studentCode } = this.registerForm.value;
    
    this.authService.register({ email, password, firstName, lastName, role, studentCode }).subscribe({
      next: () => {
        this.snackBar.open('Registration successful! Please check your email for verification code.', 'Close', { duration: 5000 });
        this.router.navigate(['/auth/verify-email'], { queryParams: { email } });
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Registration failed. Please try again.', 'Close', { duration: 5000 });
      }
    });
  }
  
  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }
  
  isStudent(): boolean {
    return this.registerForm.get('role')?.value === 'STUDENT';
  }
}
```

### features/auth/register/register.component.html
```html
<div class="register-container">
  <mat-card class="register-card">
    <mat-card-header>
      <mat-card-title>Register</mat-card-title>
      <mat-card-subtitle>Create your account to start predicting</mat-card-subtitle>
    </mat-card-header>
    
    <mat-card-content>
      <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" placeholder="student@tecsup.edu.pe" />
          <mat-error *ngIf="registerForm.get('email')?.hasError('required')">
            Email is required
          </mat-error>
          <mat-error *ngIf="registerForm.get('email')?.hasError('email')">
            Please enter a valid email
          </mat-error>
        </mat-form-field>
        
        <div class="name-fields">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>First Name</mat-label>
            <input matInput formControlName="firstName" />
            <mat-error *ngIf="registerForm.get('firstName')?.hasError('required')">
              First name is required
            </mat-error>
          </mat-form-field>
          
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Last Name</mat-label>
            <input matInput formControlName="lastName" />
            <mat-error *ngIf="registerForm.get('lastName')?.hasError('required')">
              Last name is required
            </mat-error>
          </mat-form-field>
        </div>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Role</mat-label>
          <mat-select formControlName="role">
            <mat-option value="STUDENT">Student</mat-option>
            <mat-option value="TEACHER">Teacher</mat-option>
          </mat-select>
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width" *ngIf="isStudent()">
          <mat-label>Student Code</mat-label>
          <input matInput formControlName="studentCode" placeholder="2023001" />
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Password</mat-label>
          <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" />
          <button mat-icon-button matSuffix (click)="togglePasswordVisibility()" type="button">
            <mat-icon>{{ hidePassword ? 'visibility_off' : 'visibility' }}</mat-icon>
          </button>
          <mat-error *ngIf="registerForm.get('password')?.hasError('required')">
            Password is required
          </mat-error>
          <mat-error *ngIf="registerForm.get('password')?.hasError('minlength')">
            Password must be at least 8 characters
          </mat-error>
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Confirm Password</mat-label>
          <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="confirmPassword" />
          <mat-error *ngIf="registerForm.hasError('passwordMismatch')">
            Passwords do not match
          </mat-error>
        </mat-form-field>
        
        <button mat-raised-button color="primary" type="submit" class="full-width" [disabled]="loading">
          <span *ngIf="!loading">Register</span>
          <span *ngIf="loading">Registering...</span>
        </button>
      </form>
    </mat-card-content>
    
    <mat-card-actions>
      <p class="login-link">
        Already have an account? <a routerLink="/auth/login">Login here</a>
      </p>
    </mat-card-actions>
  </mat-card>
</div>
```

### features/auth/register/register.component.scss
```scss
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 1rem;
  
  .register-card {
    max-width: 500px;
    width: 100%;
    
    .full-width {
      width: 100%;
      margin-bottom: 1rem;
    }
    
    .name-fields {
      display: flex;
      gap: 1rem;
      margin-bottom: 1rem;
      
      .half-width {
        flex: 1;
      }
    }
    
    .login-link {
      text-align: center;
      width: 100%;
      margin: 1rem 0 0;
      
      a {
        color: #1976d2;
        text-decoration: none;
        font-weight: 500;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
```

---

## 4. VERIFY EMAIL COMPONENT

### features/auth/verify-email/verify-email.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss']
})
export class VerifyEmailComponent implements OnInit {
  
  verifyForm: FormGroup;
  loading = false;
  email: string = '';
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.verifyForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      token: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });
  }
  
  ngOnInit(): void {
    this.email = this.route.snapshot.queryParamMap.get('email') || '';
    if (this.email) {
      this.verifyForm.patchValue({ email: this.email });
    }
  }
  
  onSubmit(): void {
    if (this.verifyForm.invalid) return;
    
    this.loading = true;
    
    const { email, token } = this.verifyForm.value;
    
    this.authService.verifyEmail(email, token).subscribe({
      next: (success) => {
        if (success) {
          this.snackBar.open('Email verified successfully! You can now login.', 'Close', { duration: 3000 });
          this.router.navigate(['/auth/login']);
        } else {
          this.loading = false;
          this.snackBar.open('Invalid verification code. Please try again.', 'Close', { duration: 5000 });
        }
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Verification failed. Please try again.', 'Close', { duration: 5000 });
      }
    });
  }
}
```

### features/auth/verify-email/verify-email.component.html
```html
<div class="verify-container">
  <mat-card class="verify-card">
    <mat-card-header>
      <mat-card-title>Verify Email</mat-card-title>
      <mat-card-subtitle>Enter the 6-digit code sent to your email</mat-card-subtitle>
    </mat-card-header>
    
    <mat-card-content>
      <form [formGroup]="verifyForm" (ngSubmit)="onSubmit()">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" type="email" />
          <mat-error *ngIf="verifyForm.get('email')?.hasError('required')">
            Email is required
          </mat-error>
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Verification Code</mat-label>
          <input matInput formControlName="token" type="text" maxlength="6" placeholder="123456" />
          <mat-error *ngIf="verifyForm.get('token')?.hasError('required')">
            Code is required
          </mat-error>
          <mat-error *ngIf="verifyForm.get('token')?.hasError('minlength') || verifyForm.get('token')?.hasError('maxlength')">
            Code must be 6 digits
          </mat-error>
        </mat-form-field>
        
        <button mat-raised-button color="primary" type="submit" class="full-width" [disabled]="loading">
          <span *ngIf="!loading">Verify</span>
          <span *ngIf="loading">Verifying...</span>
        </button>
      </form>
    </mat-card-content>
    
    <mat-card-actions>
      <p class="resend-link">
        Didn't receive the code? <a href="#">Resend</a>
      </p>
    </mat-card-actions>
  </mat-card>
</div>
```

### features/auth/verify-email/verify-email.component.scss
```scss
.verify-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 1rem;
  
  .verify-card {
    max-width: 450px;
    width: 100%;
    
    .full-width {
      width: 100%;
      margin-bottom: 1rem;
    }
    
    .resend-link {
      text-align: center;
      width: 100%;
      margin: 1rem 0 0;
      
      a {
        color: #1976d2;
        text-decoration: none;
        font-weight: 500;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
```

---

## 5. AUTH ROUTING MODULE

### features/auth/auth-routing.module.ts
```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'verify-email',
    component: VerifyEmailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
```

---

## 6. AUTH MODULE

### features/auth/auth.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthRoutingModule } from './auth-routing.module';
import { SharedModule } from '../../shared/shared.module';

// Components
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';

@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent,
    VerifyEmailComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AuthRoutingModule,
    SharedModule
  ]
})
export class AuthModule { }
```

---

## 7. ACTUALIZACIÓN DE APP ROUTING

### app/app-routing.module.ts
```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: '',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
  },
  {
    path: '**',
    redirectTo: '/auth/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

---

## TAREA SIGUIENTE

Una vez implementado el feature de autenticación, procede al README-06-dashboard.md para implementar el feature de dashboard.
