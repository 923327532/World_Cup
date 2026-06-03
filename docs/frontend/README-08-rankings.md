# README 08: Rankings - Feature de Rankings

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar el feature de rankings que mostrará los diferentes tipos de rankings disponibles en el sistema.

---

## 1. ESTRUCTURA DEL FEATURE RANKINGS

```
features/rankings
│
├── global-ranking
│   ├── global-ranking.component.ts
│   ├── global-ranking.component.html
│   └── global-ranking.component.scss
│
├── campus-ranking
│   ├── campus-ranking.component.ts
│   ├── campus-ranking.component.html
│   └── campus-ranking.component.scss
│
├── career-ranking
│   ├── career-ranking.component.ts
│   ├── career-ranking.component.html
│   └── career-ranking.component.scss
│
├── department-ranking
│   ├── department-ranking.component.ts
│   ├── department-ranking.component.html
│   └── department-ranking.component.scss
│
├── rankings-routing.module.ts
└── rankings.module.ts
```

---

## 2. GLOBAL RANKING COMPONENT

### features/rankings/global-ranking/global-ranking.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface RankingEntry {
  id: number;
  userId: number;
  rankingPosition: number;
  points: number;
  userName?: string;
  userEmail?: string;
}

@Component({
  selector: 'app-global-ranking',
  templateUrl: './global-ranking.component.html',
  styleUrls: ['./global-ranking.component.scss']
})
export class GlobalRankingComponent implements OnInit {
  
  rankings: RankingEntry[] = [];
  loading = true;
  displayedColumns: string[] = ['position', 'user', 'points'];
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadRankings();
  }
  
  loadRankings(): void {
    this.http.get<RankingEntry[]>(`${environment.apiUrl}/leaderboard/global?limit=100`).subscribe({
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

### features/rankings/global-ranking/global-ranking.component.html
```html
<div class="global-ranking-page">
  <div class="page-header">
    <h1>Global Ranking</h1>
    <p class="subtitle">Top 100 players across all campuses</p>
  </div>
  
  <mat-card class="ranking-card" *ngIf="!loading">
    <mat-table [dataSource]="rankings" class="ranking-table">
      <ng-container matColumnDef="position">
        <mat-header-cell *matHeaderCellDef>Position</mat-header-cell>
        <mat-cell *matCellDef="let entry">
          <div class="position-badge" [class.top-3]="entry.rankingPosition <= 3">
            {{ entry.rankingPosition }}
          </div>
        </mat-cell>
      </ng-container>
      
      <ng-container matColumnDef="user">
        <mat-header-cell *matHeaderCellDef>User</mat-header-cell>
        <mat-cell *matCellDef="let entry">
          <div class="user-info">
            <app-avatar [name]="entry.userEmail || 'User'" size="small"></app-avatar>
            <span>{{ entry.userEmail || 'User' }}</span>
          </div>
        </mat-cell>
      </ng-container>
      
      <ng-container matColumnDef="points">
        <mat-header-cell *matHeaderCellDef>Points</mat-header-cell>
        <mat-cell *matCellDef="let entry">
          <span class="points-value">{{ entry.points | points }}</span>
        </mat-cell>
      </ng-container>
      
      <mat-header-row *matHeaderRowDef="let columns"></mat-header-row>
      <mat-row *matRowDef="let row; columns: displayedColumns;"></mat-row>
    </mat-table>
  </mat-card>
  
  <div class="loading-container" *ngIf="loading">
    <mat-spinner diameter="50"></mat-spinner>
  </div>
</div>
```

### features/rankings/global-ranking/global-ranking.component.scss
```scss
.global-ranking-page {
  padding: 2rem;
  
  .page-header {
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
  
  .ranking-card {
    .ranking-table {
      width: 100%;
      
      .position-badge {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #e0e0e0;
        border-radius: 50%;
        font-weight: bold;
        
        &.top-3 {
          background-color: #ffc107;
          color: white;
        }
      }
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 0.5rem;
      }
      
      .points-value {
        font-weight: 600;
        color: #1976d2;
      }
    }
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 4rem;
  }
}
```

---

## 3. CAMPUS RANKING COMPONENT

### features/rankings/campus-ranking/campus-ranking.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface CampusRanking {
  campusId: number;
  campusName: string;
  totalPoints: number;
  userCount: number;
}

@Component({
  selector: 'app-campus-ranking',
  templateUrl: './campus-ranking.component.html',
  styleUrls: ['./campus-ranking.component.scss']
})
export class CampusRankingComponent implements OnInit {
  
  campusRankings: CampusRanking[] = [];
  loading = true;
  selectedCampus: number = 1;
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadCampusRankings();
  }
  
  loadCampusRankings(): void {
    // Load rankings for each campus
    const campuses = [1, 2, 3]; // Lima, Arequipa, Trujillo
    
    campuses.forEach(campusId => {
      this.http.get<any[]>(`${environment.apiUrl}/leaderboard/campus/${campusId}`).subscribe({
        next: (rankings) => {
          const totalPoints = rankings.reduce((sum, r) => sum + r.points, 0);
          
          this.campusRankings.push({
            campusId,
            campusName: this.getCampusName(campusId),
            totalPoints,
            userCount: rankings.length
          });
          
          this.campusRankings.sort((a, b) => b.totalPoints - a.totalPoints);
          this.loading = false;
        }
      });
    });
  }
  
  getCampusName(campusId: number): string {
    const names: { [key: number]: string } = {
      1: 'Lima',
      2: 'Arequipa',
      3: 'Trujillo'
    };
    return names[campusId] || 'Unknown';
  }
  
  selectCampus(campusId: number): void {
    this.selectedCampus = campusId;
  }
}
```

### features/rankings/campus-ranking/campus-ranking.component.html
```html
<div class="campus-ranking-page">
  <div class="page-header">
    <h1>Campus Rankings</h1>
    <p class="subtitle">Compare performance across Tecsup campuses</p>
  </div>
  
  <div class="campus-tabs">
    <button mat-button 
            *ngFor="let campus of campusRankings"
            [class.active]="selectedCampus === campus.campusId"
            (click)="selectCampus(campus.campusId)">
      {{ campus.campusName }}
    </button>
  </div>
  
  <mat-card class="ranking-card" *ngIf="!loading">
    <mat-card-content>
      <div class="campus-stats">
        <div class="stat-item">
          <div class="stat-value">{{ campusRankings.find(c => c.campusId === selectedCampus)?.totalPoints | points }}</div>
          <div class="stat-label">Total Points</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ campusRankings.find(c => c.campusId === selectedCampus)?.userCount }}</div>
          <div class="stat-label">Active Users</div>
        </div>
      </div>
    </mat-card-content>
  </mat-card>
  
  <div class="loading-container" *ngIf="loading">
    <mat-spinner diameter="50"></mat-spinner>
  </div>
</div>
```

### features/rankings/campus-ranking/campus-ranking.component.scss
```scss
.campus-ranking-page {
  padding: 2rem;
  
  .page-header {
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
  
  .campus-tabs {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
    
    button {
      &.active {
        background-color: #1976d2;
        color: white;
      }
    }
  }
  
  .ranking-card {
    .campus-stats {
      display: flex;
      gap: 2rem;
      padding: 2rem;
      
      .stat-item {
        flex: 1;
        text-align: center;
        
        .stat-value {
          font-size: 2.5rem;
          font-weight: 700;
          color: #1976d2;
        }
        
        .stat-label {
          margin-top: 0.5rem;
          color: #666;
          font-size: 1rem;
        }
      }
    }
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 4rem;
  }
}
```

---

## 4. RANKINGS ROUTING MODULE

### features/rankings/rankings-routing.module.ts
```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GlobalRankingComponent } from './global-ranking/global-ranking.component';
import { CampusRankingComponent } from './campus-ranking/campus-ranking.component';
import { CareerRankingComponent } from './career-ranking/career-ranking.component';
import { DepartmentRankingComponent } from './department-ranking/department-ranking.component';

const routes: Routes = [
  {
    path: '',
    component: GlobalRankingComponent
  },
  {
    path: 'campus',
    component: CampusRankingComponent
  },
  {
    path: 'career',
    component: CareerRankingComponent
  },
  {
    path: 'department',
    component: DepartmentRankingComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RankingsRoutingModule { }
```

---

## 5. RANKINGS MODULE

### features/rankings/rankings.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { RankingsRoutingModule } from './rankings-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { LayoutModule } from '../../layout/layout.module';

// Components
import { GlobalRankingComponent } from './global-ranking/global-ranking.component';
import { CampusRankingComponent } from './campus-ranking/campus-ranking.component';
import { CareerRankingComponent } from './career-ranking/career-ranking.component';
import { DepartmentRankingComponent } from './department-ranking/department-ranking.component';

@NgModule({
  declarations: [
    GlobalRankingComponent,
    CampusRankingComponent,
    CareerRankingComponent,
    DepartmentRankingComponent
  ],
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    RankingsRoutingModule,
    SharedModule,
    LayoutModule
  ]
})
export class RankingsModule { }
```

---

## TAREA SIGUIENTE

Una vez implementado el feature de rankings, procede al README-09-social-gamification.md para implementar los features sociales y gamificación.
