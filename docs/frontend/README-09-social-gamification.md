# README 09: Social y Gamification - Features Sociales y Gamificación

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar los features sociales (comentarios, reacciones) y gamificación (insignias, logros, premios).

---

## 1. ESTRUCTURA DEL FEATURE SOCIAL

```
features/social
│
├── live-chat
│   ├── live-chat.component.ts
│   ├── live-chat.component.html
│   └── live-chat.component.scss
│
├── reactions
│   ├── reactions.component.ts
│   ├── reactions.component.html
│   └── reactions.component.scss
│
├── social-routing.module.ts
└── social.module.ts
```

---

## 2. ESTRUCTURA DEL FEATURE GAMIFICATION

```
features/gamification
│
├── badges
│   ├── badges.component.ts
│   ├── badges.component.html
│   └── badges.component.scss
│
├── achievements
│   ├── achievements.component.ts
│   ├── achievements.component.html
│   └── achievements.component.scss
│
├── rewards
│   ├── rewards.component.ts
│   ├── rewards.component.html
│   └── rewards.component.scss
│
├── gamification-routing.module.ts
└── gamification.module.ts
```

---

## 3. BADGES COMPONENT

### features/gamification/badges/badges.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';

interface Badge {
  id: number;
  name: string;
  description: string;
  icon: string;
}

interface UserBadge {
  id: number;
  badgeId: number;
  earnedAt: string;
}

@Component({
  selector: 'app-badges',
  templateUrl: './badges.component.html',
  styleUrls: ['./badges.component.scss']
})
export class BadgesComponent implements OnInit {
  
  allBadges: Badge[] = [];
  userBadges: UserBadge[] = [];
  loading = true;
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.loadBadges();
    this.loadUserBadges();
  }
  
  loadBadges(): void {
    this.http.get<Badge[]>(`${environment.apiUrl}/badges`).subscribe({
      next: (badges) => {
        this.allBadges = badges;
      }
    });
  }
  
  loadUserBadges(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.http.get<UserBadge[]>(`${environment.apiUrl}/badges/user/${user.id}`).subscribe({
        next: (userBadges) => {
          this.userBadges = userBadges;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    }
  }
  
  hasBadge(badgeId: number): boolean {
    return this.userBadges.some(ub => ub.badgeId === badgeId);
  }
  
  getEarnedDate(badgeId: number): string | null {
    const userBadge = this.userBadges.find(ub => ub.badgeId === badgeId);
    return userBadge ? new Date(userBadge.earnedAt).toLocaleDateString() : null;
  }
}
```

### features/gamification/badges/badges.component.html
```html
<div class="badges-page">
  <div class="page-header">
    <h1>My Badges</h1>
    <p class="subtitle">Achievements you've unlocked</p>
  </div>
  
  <div class="badges-grid" *ngIf="!loading">
    <app-badge 
      *ngFor="let badge of allBadges"
      [icon]="badge.icon"
      [name]="badge.name"
      [description]="badge.description"
      [earned]="hasBadge(badge.id)">
    </app-badge>
  </div>
  
  <div class="loading-container" *ngIf="loading">
    <mat-spinner diameter="50"></mat-spinner>
  </div>
</div>
```

### features/gamification/badges/badges.component.scss
```scss
.badges-page {
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
  
  .badges-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 4rem;
  }
}
```

---

## 4. ACHIEVEMENTS COMPONENT

### features/gamification/achievements/achievements.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../core/services/auth.service';

interface Achievement {
  id: number;
  badgeId: number;
  requiredPoints: number;
  requiredStreak: number;
  requiredPosition: number;
  progress: number;
  completed: boolean;
}

@Component({
  selector: 'app-achievements',
  templateUrl: './achievements.component.html',
  styleUrls: ['./achievements.component.scss']
})
export class AchievementsComponent implements OnInit {
  
  achievements: Achievement[] = [];
  loading = true;
  userPoints = 0;
  userStreak = 0;
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.loadAchievements();
    this.loadUserStats();
  }
  
  loadAchievements(): void {
    this.http.get<Achievement[]>(`${environment.apiUrl}/achievements`).subscribe({
      next: (achievements) => {
        this.achievements = achievements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
  
  loadUserStats(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.http.get<any>(`${environment.apiUrl}/scoring/user/${user.id}`).subscribe({
        next: (stats) => {
          this.userPoints = stats.totalPoints;
        }
      });
    }
  }
  
  getProgress(achievement: Achievement): number {
    if (achievement.requiredPoints) {
      return Math.min((this.userPoints / achievement.requiredPoints) * 100, 100);
    }
    if (achievement.requiredStreak) {
      return Math.min((this.userStreak / achievement.requiredStreak) * 100, 100);
    }
    return 0;
  }
}
```

### features/gamification/achievements/achievements.component.html
```html
<div class="achievements-page">
  <div class="page-header">
    <h1>Achievements</h1>
    <p class="subtitle">Track your progress and unlock rewards</p>
  </div>
  
  <div class="stats-summary">
    <div class="stat-card">
      <div class="stat-value">{{ userPoints | points }}</div>
      <div class="stat-label">Total Points</div>
    </div>
    <div class="stat-card">
      <div class="stat-value">{{ userStreak }}</div>
      <div class="stat-label">Current Streak</div>
    </div>
  </div>
  
  <div class="achievements-list" *ngIf="!loading">
    <mat-card class="achievement-card" *ngFor="let achievement of achievements">
      <mat-card-content>
        <div class="achievement-header">
          <h3>{{ achievement.badgeId }}</h3>
          <mat-chip [class.completed]="achievement.completed">
            {{ achievement.completed ? 'Completed' : 'In Progress' }}
          </mat-chip>
        </div>
        
        <div class="achievement-progress">
          <mat-progress-bar 
            mode="determinate" 
            [value]="getProgress(achievement)"
            [color]="achievement.completed ? 'primary' : 'accent'">
          </mat-progress-bar>
          <div class="progress-text">
            {{ getProgress(achievement) | number:'1.0-0' }}%
          </div>
        </div>
        
        <div class="achievement-requirements">
          <p *ngIf="achievement.requiredPoints">
            Required: {{ achievement.requiredPoints | points }} points
          </p>
          <p *ngIf="achievement.requiredStreak">
            Required: {{ achievement.requiredStreak }} streak
          </p>
        </div>
      </mat-card-content>
    </mat-card>
  </div>
  
  <div class="loading-container" *ngIf="loading">
    <mat-spinner diameter="50"></mat-spinner>
  </div>
</div>
```

### features/gamification/achievements/achievements.component.scss
```scss
.achievements-page {
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
  
  .stats-summary {
    display: flex;
    gap: 1.5rem;
    margin-bottom: 2rem;
    
    .stat-card {
      flex: 1;
      padding: 1.5rem;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12px;
      color: white;
      text-align: center;
      
      .stat-value {
        font-size: 2rem;
        font-weight: 700;
      }
      
      .stat-label {
        margin-top: 0.5rem;
        font-size: 1rem;
        opacity: 0.9;
      }
    }
  }
  
  .achievements-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    
    .achievement-card {
      .achievement-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
        
        h3 {
          margin: 0;
          font-size: 1.125rem;
          font-weight: 600;
        }
        
        mat-chip {
          &.completed {
            background-color: #4caf50;
            color: white;
          }
        }
      }
      
      .achievement-progress {
        margin-bottom: 1rem;
        
        .progress-text {
          text-align: right;
          font-size: 0.875rem;
          color: #666;
          margin-top: 0.25rem;
        }
      }
      
      .achievement-requirements {
        p {
          margin: 0;
          color: #666;
          font-size: 0.875rem;
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

## 5. REWARDS COMPONENT

### features/gamification/rewards/rewards.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface Reward {
  id: number;
  position: number;
  title: string;
  description: string;
}

@Component({
  selector: 'app-rewards',
  templateUrl: './rewards.component.html',
  styleUrls: ['./rewards.component.scss'
})
export class RewardsComponent implements OnInit {
  
  rewards: Reward[] = [];
  loading = true;
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadRewards();
  }
  
  loadRewards(): void {
    this.http.get<Reward[]>(`${environment.apiUrl}/rewards`).subscribe({
      next: (rewards) => {
        this.rewards = rewards;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
```

### features/gamification/rewards/rewards.component.html
```html
<div class="rewards-page">
  <div class="page-header">
    <h1>Rewards</h1>
    <p class="subtitle">Prizes for top performers</p>
  </div>
  
  <div class="rewards-list" *ngIf="!loading">
    <mat-card class="reward-card" *ngFor="let reward of rewards">
      <div class="reward-position">{{ reward.position }}</div>
      <div class="reward-content">
        <h3>{{ reward.title }}</h3>
        <p>{{ reward.description }}</p>
      </div>
    </mat-card>
  </div>
  
  <div class="loading-container" *ngIf="loading">
    <mat-spinner diameter="50"></mat-spinner>
  </div>
</div>
```

### features/gamification/rewards/rewards.component.scss
```scss
.rewards-page {
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
  
  .rewards-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    
    .reward-card {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      padding: 1.5rem;
      
      .reward-position {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
        color: white;
        border-radius: 50%;
        font-size: 1.5rem;
        font-weight: 700;
      }
      
      .reward-content {
        flex: 1;
        
        h3 {
          margin: 0 0 0.5rem;
          font-size: 1.25rem;
          font-weight: 600;
        }
        
        p {
          margin: 0;
          color: #666;
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

## 6. GAMIFICATION MODULE

### features/gamification/gamification.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { GamificationRoutingModule } from './gamification-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { LayoutModule } from '../../layout/layout.module';

// Components
import { BadgesComponent } from './badges/badges.component';
import { AchievementsComponent } from './achievements/achievements.component';
import { RewardsComponent } from './rewards/rewards.component';

@NgModule({
  declarations: [
    BadgesComponent,
    AchievementsComponent,
    RewardsComponent
  ],
  imports: [
    CommonModule,
    MatProgressBarModule,
    MatChipsModule,
    GamificationRoutingModule,
    SharedModule,
    LayoutModule
  ]
})
export class GamificationModule { }
```

---

## TAREA SIGUIENTE

Una vez implementados los features sociales y gamificación, procede al README-10-integracion.md para implementar la integración con backend y WebSockets.
