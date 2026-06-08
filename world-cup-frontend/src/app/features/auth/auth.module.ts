import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { AuthRoutingModule } from './auth-routing.module';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { VerifyEmailComponent } from './verify-email/verify-email.component';

@NgModule({
  declarations: [LoginComponent, RegisterComponent, VerifyEmailComponent],
  imports: [SharedModule, ReactiveFormsModule, AuthRoutingModule]
})
export class AuthModule {}
