# README 06: Dashboard - Feature de Dashboard Principal

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar el dashboard principal que mostrará el estado general del mundial, partidos del día, usuarios conectados y estadísticas en tiempo real.

---

## 1. ESTRUCTURA DEL FEATURE DASHBOARD

```
features/dashboard
│
├── dashboard-page
│   ├── dashboard-page.component.ts
│   ├── dashboard-page.component.html
│   └── dashboard-page.component.scss
│
├── widgets
│   ├── live-users
│   │   ├── live-users.component.ts
│   │   ├── live-users.component.html
│   │   └── live-users.component.scss
│   ├── top-ranking
│   │   ├── top-ranking.component.ts
│   │   ├── top-ranking.component.html
│   │   └── top-ranking.component.scss
│   ├── active-matches
│   │   ├── active-matches.component.ts
│   │   ├── active-matches.component.html
│   │   └── active-matches.component.scss
│   ├── prediction-stats
│   │   ├── prediction-stats.component.ts
│   │   ├── prediction-stats.component.html
│   │   └── prediction-stats.component.scss
│   └── department-ranking
│       ├── department-ranking.component.ts
│       ├── department-ranking.component.html
│       └── department-ranking.component.scss
│
├── dashboard-routing.module.ts
└── dashboard.module.ts
```

---

## 2. DASHBOARD PAGE COMPONENT

### features/dashboard/dashboard-page/dashboard-page.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../core/services/websocket.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss']
})
export class DashboardPageComponent implements OnInit {
  
  currentUser: any;
  liveUsers = 0;
  
  constructor(
    private authService: AuthService,
    private webSocketService: WebSocketService
  ) {}
  
  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.webSocketService.connect();
    
    // Subscribe to live users updates
    this.webSocketService.subscribeToNotifications(this.currentUser.id).subscribe(data => {
      if (data.type === 'LIVE_USERS') {
        this.liveUsers = data.count;
      }
    });
  }
  
  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }
}
```

### features/dashboard/dashboard-page/dashboard-page.component.html
```html
<div class="dashboard-page">
  <div class="dashboard-header">
    <h1>Welcome back, {{ currentUser?.firstName || 'User' }}!</h1>
    <p class="subtitle">Here's what's happening in the World Cup</p>
  </div>
  
  <div class="dashboard-widgets">
    <div class="widget-row">
      <app-live-users></app-live-users>
      <app-prediction-stats></app-prediction-stats>
    </div>
    
    <div class="widget-row">
      <app-active-matches></app-active-matches>
      <app-top-ranking></app-top-ranking>
    </div>
    
    <div class="widget-row full-width">
      <app-department-ranking></app-department-ranking>
    </div>
  </div>
</div>
```

### features/dashboard/dashboard-page/dashboard-page.component.scss
```scss
.dashboard-page {
  padding: 2rem;
  
  .dashboard-header {
    margin-bottom: 2rem;
    
    h1 {
      margin: 0 0 0.5rem;
      font-size: 2rem;
      font-weight: 700;
    }
    
    .subtitle {
      margin: 0;
      color: #666;
      font-size: 1.125rem;
    }
  }
  
  .dashboard-widgets {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
    
    .widget-row {
      display: flex;
      gap: 1.5rem;
      
      &.full-width {
        flex-direction: column;
      }
    }
  }
}
```

---

## 3. WIDGET: LIVE USERS

### features/dashboard/widgets/live-users/live-users.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { WebSocketService } from '../../../core/services/websocket.service';

@Component({
  selector: 'app-live-users',
  templateUrl: './live-users.component.html',
  styleUrls: ['./live-users.component.scss']
})
export class LiveUsersComponent implements OnInit {
  
  liveUsers = 0;
  loading = true;
  
  constructor(private webSocketService: WebSocketService) {}
  
  ngOnInit(): void {
    // Simulate initial data
    this.liveUsers = 1234;
    this.loading = false;
    
    // Subscribe to live updates
    this.webSocketService.subscribeToNotifications(0).subscribe(data => {
      if (data.type === 'LIVE_USERS') {
        this.liveUsers = data.count;
      }
    });
  }
}
```

### features/dashboard/widgets/live-users/live-users.component.html
```html
<mat-card class="widget-card">
  <mat-card-header>
    <mat-card-title>
      <mat-icon>people</mat-icon>
      Live Users
    </mat-card-title>
  </mat-card-header>
  
  <mat-card-content>
    <div class="live-users-display" *ngIf="!loading">
      <div class="users-count">{{ liveUsers }}</div>
      <div class="users-label">Users Online</div>
    </div>
    
    <div class="loading-container" *ngIf="loading">
      <mat-spinner diameter="40"></mat-spinner>
    </div>
  </mat-card-content>
</mat-card>
```

### features/dashboard/widgets/live-users/live-users.component.scss
```scss
.widget-card {
  flex: 1;
  
  mat-card-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    
    mat-icon {
      color: #1976d2;
    }
  }
  
  .live-users-display {
    text-align: center;
    padding: 2rem 0;
    
    .users-count {
      font-size: 3rem;
      font-weight: 700;
      color: #1976d2;
    }
    
    .users-label {
      font-size: 1rem;
      color: #666;
      margin-top: 0.5rem;
    }
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 2rem 0;
  }
}
```

---

## 4. WIDGET: ACTIVE MATCHES

### features/dashboard/widgets/active-matches/active-matches.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface Match {
  id: number;
  homeTeam: string;
  awayTeam: string;
  kickoffTime: string;
  homeScore: number;
  awayScore: number;
  status: string;
}

@Component({
  selector: 'app-active-matches',
  templateUrl: './active-matches.component.html',
  styleUrls: ['./active-matches.component.scss']
})
export class ActiveMatchesComponent implements OnInit {
  
  matches: Match[] = [];
  loading = true;
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadMatches();
  }
  
  loadMatches(): void {
    const today = new Date().toISOString().split('T')[0];
    
    this.http.get<Match[]>(`${environment.apiUrl}/matches/date/${today}`).subscribe({
      next: (matches) => {
        this.matches = matches.filter(m => m.status === 'LIVE' || m.status === 'SCHEDULED').slice(0, 5);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
```

### features/dashboard/widgets/active-matches/active-matches.component.html
```html
<mat-card class="widget-card">
  <mat-card-header>
    <mat-card-title>
      <mat-icon>sports_soccer</mat-icon>
      Today's Matches
    </mat-card-title>
  </mat-card-header>
  
  <mat-card-content>
    <div class="matches-list" *ngIf="!loading">
      <app-match-card 
        *ngFor="let match of matches" 
        [match]="match"
        [clickable]="true">
      </app-match-card>
      
      <div class="no-matches" *ngIf="matches.length === 0">
        <p>No matches scheduled for today</p>
      </div>
    </div>
    
    <div class="loading-container" *ngIf="loading">
      <mat-spinner diameter="40"></mat-spinner>
    </div>
  </mat-card-content>
</mat-card>
```

### features/dashboard/widgets/active-matches/active-matches.component.scss
```scss
.widget-card {
  flex: 1;
  
  mat-card-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    
    mat-icon {
      color: #1976d2;
    }
  }
  
  .matches-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  
  .no-matches {
    text-align: center;
    padding: 2rem;
    color: #666;
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 2rem 0;
  }
}
```

---

## 5. WIDGET: TOP RANKING

### features/dashboard/widgets/top-ranking/top-ranking.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface RankingEntry {
  id: number;
  userId: number;
  rankingPosition: number;
  points: number;
}

@Component({
  selector: 'app-top-ranking',
  templateUrl: './top-ranking.component.html',
  styleUrls: ['./top-ranking.component.scss']
})
export class TopRankingComponent implements OnInit {
  
  rankings: RankingEntry[] = [];
  loading = true;
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadRankings();
  }
  
  loadRankings(): void {
    this.http.get<RankingEntry[]>(`${environment.apiUrl}/leaderboard/global?limit=5`).subscribe({
      next: (rankings) => {
        this.rankings = rankings;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
```

### features/dashboard/widgets/top-ranking/top-ranking.component.html
```html
<mat-card class="widget-card">
  <mat-card-header>
    <mat-card-title>
      <mat-icon>emoji_events</mat-icon>
      Top 5 Ranking
    </mat-card-title>
  </mat-card-header>
  
  <mat-card-content>
    <div class="rankings-list" *ngIf="!loading">
      <div class="ranking-item" *ngFor="let entry of rankings">
        <div class="rank-position">{{ entry.rankingPosition }}</div>
        <div class="rank-info">
          <div class="rank-points">{{ entry.points | points }} pts</div>
        </div>
      </div>
      
      <div class="no-rankings" *ngIf="rankings.length === 0">
        <p>No rankings available</p>
      </div>
    </div>
    
    <div class="loading-container" *ngIf="loading">
      <mat-spinner diameter="40"></mat-spinner>
    </div>
  </mat-card-content>
</mat-card>
```

### features/dashboard/widgets/top-ranking/top-ranking.component.scss
```scss
.widget-card {
  flex: 1;
  
  mat-card-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    
    mat-icon {
      color: #ffc107;
    }
  }
  
  .rankings-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .ranking-item {
    display: flex;
    align-items: center;
    padding: 0.75rem;
    background-color: #f5f5f5;
    border-radius: 8px;
    
    .rank-position {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #1976d2;
      color: white;
      border-radius: 50%;
      font-weight: bold;
      margin-right: 1rem;
    }
    
    .rank-info {
      flex: 1;
      
      .rank-points {
        font-weight: 600;
        color: #1976d2;
      }
    }
  }
  
  .no-rankings {
    text-align: center;
    padding: 2rem;
    color: #666;
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 2rem 0;
  }
}
```

---

## 6. DASHBOARD ROUTING MODULE

### features/dashboard/dashboard-routing.module.ts
```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardPageComponent } from './dashboard-page/dashboard-page.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
```

---

## 7. DASHBOARD MODULE

### features/dashboard/dashboard.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { LayoutModule } from '../../layout/layout.module';

// Components
import { DashboardPageComponent } from './dashboard-page/dashboard-page.component';
import { LiveUsersComponent } from './widgets/live-users/live-users.component';
import { ActiveMatchesComponent } from './widgets/active-matches/active-matches.component';
import { TopRankingComponent } from './widgets/top-ranking/top-ranking.component';
import { PredictionStatsComponent } from './widgets/prediction-stats/prediction-stats.component';
import { DepartmentRankingComponent } from './widgets/department-ranking/department-ranking.component';

@NgModule({
  declarations: [
    DashboardPageComponent,
    LiveUsersComponent,
    ActiveMatchesComponent,
    TopRankingComponent,
    PredictionStatsComponent,
    DepartmentRankingComponent
  ],
  imports: [
    CommonModule,
    DashboardRoutingModule,
    SharedModule,
    LayoutModule
  ]
})
export class DashboardModule { }
```

---

## TAREA SIGUIENTE

Una vez implementado el dashboard, procede al README-07-matches-predictions.md para implementar los features de partidos y predicciones.
