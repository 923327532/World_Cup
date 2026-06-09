import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { DashboardLayoutComponent } from './layout/dashboard-layout/dashboard-layout.component';
import { PublicLayoutComponent } from './layout/public-layout/public-layout.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () => import('./features/landing/landing.module').then((m) => m.LandingModule)
  },
  {
    path: 'auth',
    component: PublicLayoutComponent,
    loadChildren: () => import('./features/auth/auth.module').then((m) => m.AuthModule)
  },
  {
    path: '',
    component: DashboardLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.module').then((m) => m.DashboardModule)
      },
      {
        path: 'matches',
        loadChildren: () => import('./features/matches/matches.module').then((m) => m.MatchesModule)
      },
      {
        path: 'predictions',
        loadChildren: () => import('./features/predictions/predictions.module').then((m) => m.PredictionsModule)
      },
      {
        path: 'rankings',
        loadChildren: () => import('./features/rankings/rankings.module').then((m) => m.RankingsModule)
      },
      {
        path: 'social',
        loadChildren: () => import('./features/social/social.module').then((m) => m.SocialModule)
      },
      {
        path: 'gamification',
        loadChildren: () => import('./features/gamification/gamification.module').then((m) => m.GamificationModule)
      }
    ]
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
