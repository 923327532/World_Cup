# README 03: Shared - Componentes Compartidos

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar los componentes compartidos que serán reutilizados en toda la aplicación.

---

## 1. ESTRUCTURA DEL MÓDULO SHARED

```
shared
│
├── components
│   ├── loading
│   │   ├── loading.component.ts
│   │   ├── loading.component.html
│   │   └── loading.component.scss
│   ├── avatar
│   │   ├── avatar.component.ts
│   │   ├── avatar.component.html
│   │   └── avatar.component.scss
│   ├── badge
│   │   ├── badge.component.ts
│   │   ├── badge.component.html
│   │   └── badge.component.scss
│   ├── ranking-card
│   │   ├── ranking-card.component.ts
│   │   ├── ranking-card.component.html
│   │   └── ranking-card.component.scss
│   ├── prediction-card
│   │   ├── prediction-card.component.ts
│   │   ├── prediction-card.component.html
│   │   └── prediction-card.component.scss
│   ├── match-card
│   │   ├── match-card.component.ts
│   │   ├── match-card.component.html
│   │   └── match-card.component.scss
│   ├── comment-card
│   │   ├── comment-card.component.ts
│   │   ├── comment-card.component.html
│   │   └── comment-card.component.scss
│   └── notification-card
│       ├── notification-card.component.ts
│       ├── notification-card.component.html
│       └── notification-card.component.scss
│
├── pipes
│   ├── date.pipe.ts
│   └── points.pipe.ts
│
├── directives
│   └── click-outside.directive.ts
│
├── material
│   └── material.module.ts
│
└── shared.module.ts
```

---

## 2. MATERIAL MODULE

### shared/material/material.module.ts
```typescript
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';

@NgModule({
  declarations: [],
  imports: [
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatToolbarModule,
    MatSidenavModule,
    MatMenuModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatTabsModule,
    MatChipsModule,
    MatBadgeModule,
    MatTooltipModule
  ],
  exports: [
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
    MatToolbarModule,
    MatSidenavModule,
    MatMenuModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatTabsModule,
    MatChipsModule,
    MatBadgeModule,
    MatTooltipModule
  ]
})
export class MaterialModule { }
```

---

## 3. COMPONENTES COMPARTIDOS

### components/loading/loading.component.ts
```typescript
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss']
})
export class LoadingComponent {
  @Input() message = 'Loading...';
}
```

### components/loading/loading.component.html
```html
<div class="loading-container">
  <mat-spinner diameter="50"></mat-spinner>
  <p>{{ message }}</p>
</div>
```

### components/loading/loading.component.scss
```scss
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  
  p {
    margin-top: 1rem;
    color: #666;
  }
}
```

---

### components/avatar/avatar.component.ts
```typescript
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.scss']
})
export class AvatarComponent {
  @Input() imageUrl?: string;
  @Input() name?: string;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';
  
  getInitials(): string {
    if (!this.name) return '?';
    const names = this.name.split(' ');
    return names.map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }
  
  getSizeClass(): string {
    return `avatar-${this.size}`;
  }
}
```

### components/avatar/avatar.component.html
```html
<div class="avatar-container" [ngClass]="getSizeClass()">
  <img *ngIf="imageUrl" [src]="imageUrl" [alt]="name" />
  <span *ngIf="!imageUrl" class="avatar-initials">{{ getInitials() }}</span>
</div>
```

### components/avatar/avatar.component.scss
```scss
.avatar-container {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #e0e0e0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .avatar-initials {
    font-weight: bold;
    color: #666;
  }
}

.avatar-small {
  width: 32px;
  height: 32px;
  font-size: 12px;
}

.avatar-medium {
  width: 48px;
  height: 48px;
  font-size: 16px;
}

.avatar-large {
  width: 64px;
  height: 64px;
  font-size: 20px;
}
```

---

### components/badge/badge.component.ts
```typescript
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-badge',
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.scss']
})
export class BadgeComponent {
  @Input() icon!: string;
  @Input() name!: string;
  @Input() description?: string;
  @Input() earned = false;
}
```

### components/badge/badge.component.html
```html
<div class="badge-container" [class.earned]="earned">
  <div class="badge-icon">{{ icon }}</div>
  <div class="badge-info">
    <h3>{{ name }}</h3>
    <p *ngIf="description">{{ description }}</p>
  </div>
  <mat-icon *ngIf="earned" class="check-icon">check_circle</mat-icon>
</div>
```

### components/badge/badge.component.scss
```scss
.badge-container {
  display: flex;
  align-items: center;
  padding: 1rem;
  border-radius: 8px;
  background-color: #f5f5f5;
  margin-bottom: 1rem;
  opacity: 0.5;
  transition: all 0.3s;
  
  &.earned {
    opacity: 1;
    background-color: #e8f5e9;
    border: 2px solid #4caf50;
  }
  
  .badge-icon {
    font-size: 2rem;
    margin-right: 1rem;
  }
  
  .badge-info {
    flex: 1;
    
    h3 {
      margin: 0;
      font-size: 1rem;
    }
    
    p {
      margin: 0.25rem 0 0;
      font-size: 0.875rem;
      color: #666;
    }
  }
  
  .check-icon {
    color: #4caf50;
  }
}
```

---

### components/match-card/match-card.component.ts
```typescript
import { Component, Input } from '@angular/core';

interface Match {
  id: number;
  homeTeam: string;
  awayTeam: string;
  kickoffTime: string;
  homeScore?: number;
  awayScore?: number;
  status: string;
}

@Component({
  selector: 'app-match-card',
  templateUrl: './match-card.component.html',
  styleUrls: ['./match-card.component.scss']
})
export class MatchCardComponent {
  @Input() match!: Match;
  @Input() clickable = false;
  
  isLive(): boolean {
    return this.match.status === 'LIVE';
  }
  
  isFinished(): boolean {
    return this.match.status === 'FINISHED';
  }
  
  isUpcoming(): boolean {
    return this.match.status === 'SCHEDULED';
  }
}
```

### components/match-card/match-card.component.html
```html
<mat-card class="match-card" [class.clickable]="clickable">
  <div class="match-status" [class.live]="isLive()" [class.finished]="isFinished()">
    <span *ngIf="isLive()" class="live-indicator">● LIVE</span>
    <span *ngIf="isFinished()">FT</span>
  </div>
  
  <div class="match-content">
    <div class="team home-team">
      <span class="team-name">{{ match.homeTeam }}</span>
      <span class="team-score" *ngIf="match.homeScore !== undefined">{{ match.homeScore }}</span>
    </div>
    
    <div class="match-info">
      <div class="kickoff-time">{{ match.kickoffTime | date:'shortTime' }}</div>
      <div class="match-date">{{ match.kickoffTime | date:'shortDate' }}</div>
    </div>
    
    <div class="team away-team">
      <span class="team-score" *ngIf="match.awayScore !== undefined">{{ match.awayScore }}</span>
      <span class="team-name">{{ match.awayTeam }}</span>
    </div>
  </div>
</mat-card>
```

### components/match-card/match-card.component.scss
```scss
.match-card {
  margin-bottom: 1rem;
  
  &.clickable {
    cursor: pointer;
    transition: transform 0.2s;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
  }
  
  .match-status {
    padding: 0.5rem 1rem;
    text-align: center;
    font-weight: bold;
    font-size: 0.75rem;
    text-transform: uppercase;
    
    .live-indicator {
      color: #f44336;
      animation: pulse 1.5s infinite;
    }
    
    &.live {
      background-color: #ffebee;
    }
    
    &.finished {
      background-color: #f5f5f5;
      color: #666;
    }
  }
  
  .match-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1rem;
  }
  
  .team {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    &.home-team {
      align-items: flex-start;
    }
    
    &.away-team {
      align-items: flex-end;
    }
    
    .team-name {
      font-weight: 500;
      margin-bottom: 0.25rem;
    }
    
    .team-score {
      font-size: 1.5rem;
      font-weight: bold;
      color: #1976d2;
    }
  }
  
  .match-info {
    flex: 0 0 auto;
    text-align: center;
    padding: 0 1rem;
    
    .kickoff-time {
      font-size: 1.25rem;
      font-weight: bold;
    }
    
    .match-date {
      font-size: 0.875rem;
      color: #666;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
```

---

## 4. PIPES

### pipes/date.pipe.ts
```typescript
import { Pipe, PipeTransform } from '@angular/core';
import { format } from 'date-fns';

@Pipe({
  name: 'customDate'
})
export class DatePipe implements PipeTransform {
  transform(value: string, formatStr: string = 'PPP'): string {
    if (!value) return '';
    return format(new Date(value), formatStr);
  }
}
```

### pipes/points.pipe.ts
```typescript
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'points'
})
export class PointsPipe implements PipeTransform {
  transform(value: number): string {
    if (value === null || value === undefined) return '0';
    return value.toLocaleString();
  }
}
```

---

## 5. DIRECTIVES

### directives/click-outside.directive.ts
```typescript
import { Directive, ElementRef, Output, EventEmitter, HostListener } from '@angular/core';

@Directive({
  selector: '[appClickOutside]'
})
export class ClickOutsideDirective {
  
  @Output() appClickOutside = new EventEmitter<void>();
  
  constructor(private elementRef: ElementRef) {}
  
  @HostListener('document:click', ['$event.target'])
  public onClick(target: HTMLElement): void {
    const clickedInside = this.elementRef.nativeElement.contains(target);
    if (!clickedInside) {
      this.appClickOutside.emit();
    }
  }
}
```

---

## 6. SHARED MODULE

### shared/shared.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from './material/material.module';

// Components
import { LoadingComponent } from './components/loading/loading.component';
import { AvatarComponent } from './components/avatar/avatar.component';
import { BadgeComponent } from './components/badge/badge.component';
import { MatchCardComponent } from './components/match-card/match-card.component';

// Pipes
import { DatePipe } from './pipes/date.pipe';
import { PointsPipe } from './pipes/points.pipe';

// Directives
import { ClickOutsideDirective } from './directives/click-outside.directive';

@NgModule({
  declarations: [
    LoadingComponent,
    AvatarComponent,
    BadgeComponent,
    MatchCardComponent,
    DatePipe,
    PointsPipe,
    ClickOutsideDirective
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    CommonModule,
    MaterialModule,
    LoadingComponent,
    AvatarComponent,
    BadgeComponent,
    MatchCardComponent,
    DatePipe,
    PointsPipe,
    ClickOutsideDirective
  ]
})
export class SharedModule { }
```

---

## TAREA SIGUIENTE

Una vez implementados los componentes compartidos, procede al README-04-layout.md para implementar los layouts de la aplicación.
