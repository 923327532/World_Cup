import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';
import { DashboardLayoutComponent } from './layout/dashboard-layout/dashboard-layout.component';
import { PublicLayoutComponent } from './layout/public-layout/public-layout.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () => import('./features/landing/landing.module').then((m) => m.LandingModule),
  },
  {
    path: 'auth',
    component: PublicLayoutComponent,
    loadChildren: () => import('./features/auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'app',
    component: DashboardLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.module').then((m) => m.DashboardModule),
      },
      {
        path: 'matches',
        loadChildren: () =>
          import('./features/matches/matches.module').then((m) => m.MatchesModule),
      },
      {
        path: 'predictions',
        loadChildren: () =>
          import('./features/predictions/predictions.module').then((m) => m.PredictionsModule),
      },
      {
        path: 'rankings',
        loadChildren: () =>
          import('./features/rankings/rankings.module').then((m) => m.RankingsModule),
      },
      {
        path: 'social',
        loadChildren: () => import('./features/social/social.module').then((m) => m.SocialModule),
      },
      {
        path: 'gamification',
        loadChildren: () =>
          import('./features/gamification/gamification.module').then((m) => m.GamificationModule),
      },
      {
        path: 'account',
        loadChildren: () =>
          import('./features/account/account.module').then((m) => m.AccountModule),
      },
      {
        path: 'notifications',
        loadChildren: () =>
          import('./features/notifications/notifications.module').then((m) => m.NotificationsModule),
      },
      {
        path: 'organization',
        loadChildren: () =>
          import('./features/organization/organization.module').then((m) => m.OrganizationModule),
      },
      {
        path: 'groups',
        loadChildren: () =>
          import('./features/groups/groups.module').then((m) => m.GroupsModule),
      },
    ],
  },
  {
    path: 'admin',
    component: DashboardLayoutComponent,
    canActivate: [AuthGuard, AdminGuard],
    loadChildren: () => import('./features/admin/admin.module').then((m) => m.AdminModule),
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
