# README 04: Layout - Layouts de la Aplicación

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar los layouts que definirán la estructura visual de la aplicación (navbar, sidebar, footer, dashboard layout, public layout).

---

## 1. ESTRUCTURA DEL MÓDULO LAYOUT

```
layout
│
├── navbar
│   ├── navbar.component.ts
│   ├── navbar.component.html
│   └── navbar.component.scss
│
├── sidebar
│   ├── sidebar.component.ts
│   ├── sidebar.component.html
│   └── sidebar.component.scss
│
├── footer
│   ├── footer.component.ts
│   ├── footer.component.html
│   └── footer.component.scss
│
├── dashboard-layout
│   ├── dashboard-layout.component.ts
│   ├── dashboard-layout.component.html
│   └── dashboard-layout.component.scss
│
├── public-layout
│   ├── public-layout.component.ts
│   ├── public-layout.component.html
│   └── public-layout.component.scss
│
└── layout.module.ts
```

---

## 2. NAVBAR

### layout/navbar/navbar.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Observable } from 'rxjs';
import { User } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  
  currentUser$: Observable<User | null>;
  unreadNotifications = 0;
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.currentUser$ = this.authService.currentUser$;
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
  
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
```

### layout/navbar/navbar.component.html
```html
<mat-toolbar class="navbar" color="primary">
  <button mat-icon-button (click)="navigateTo('/dashboard')">
    <mat-icon>home</mat-icon>
  </button>
  
  <span class="navbar-title">Tecsup World Cup 2026</span>
  
  <span class="navbar-spacer"></span>
  
  <div class="navbar-actions" *ngIf="currentUser$ | async as user">
    <button mat-icon-button [matMenuTriggerFor]="notificationMenu">
      <mat-icon [matBadge]="unreadNotifications" matBadgeColor="warn" *ngIf="unreadNotifications > 0">
        notifications
      </mat-icon>
      <mat-icon *ngIf="unreadNotifications === 0">notifications_none</mat-icon>
    </button>
    
    <mat-menu #notificationMenu="matMenu">
      <button mat-menu-item>View All Notifications</button>
      <button mat-menu-item>Mark All as Read</button>
    </mat-menu>
    
    <button mat-icon-button [matMenuTriggerFor]="userMenu">
      <app-avatar [name]="user.email" size="small"></app-avatar>
    </button>
    
    <mat-menu #userMenu="matMenu">
      <button mat-menu-item (click)="navigateTo('/profile')">Profile</button>
      <button mat-menu-item (click)="navigateTo('/rankings')">My Rankings</button>
      <button mat-menu-item (click)="navigateTo('/achievements')">Achievements</button>
      <mat-divider></mat-divider>
      <button mat-menu-item (click)="logout()">Logout</button>
    </mat-menu>
  </div>
</mat-toolbar>
```

### layout/navbar/navbar.component.scss
```scss
.navbar {
  display: flex;
  align-items: center;
  padding: 0 1rem;
  
  .navbar-title {
    margin-left: 1rem;
    font-size: 1.25rem;
    font-weight: 500;
  }
  
  .navbar-spacer {
    flex: 1;
  }
  
  .navbar-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
}
```

---

## 3. SIDEBAR

### layout/sidebar/sidebar.component.ts
```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface MenuItem {
  icon: string;
  label: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  
  menuItems: MenuItem[] = [
    { icon: 'dashboard', label: 'Dashboard', route: '/dashboard' },
    { icon: 'sports_soccer', label: 'Matches', route: '/matches' },
    { icon: 'predictions', label: 'Predictions', route: '/predictions' },
    { icon: 'leaderboard', label: 'Rankings', route: '/rankings' },
    { icon: 'emoji_events', label: 'Achievements', route: '/achievements' },
    { icon: 'person', label: 'Profile', route: '/profile' }
  ];
  
  constructor(private router: Router) {}
  
  navigate(route: string): void {
    this.router.navigate([route]);
  }
  
  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }
}
```

### layout/sidebar/sidebar.component.html
```html
<mat-sidenav-container class="sidebar-container">
  <mat-sidenav mode="side" opened class="sidebar">
    <div class="sidebar-header">
      <h3>Menu</h3>
    </div>
    
    <mat-nav-list>
      <a mat-list-item 
         *ngFor="let item of menuItems"
         (click)="navigate(item.route)"
         [class.active]="isActive(item.route)">
        <mat-icon mat-list-icon>{{ item.icon }}</mat-icon>
        <span mat-line>{{ item.label }}</span>
      </a>
    </mat-nav-list>
  </mat-sidenav>
  
  <mat-sidenav-content>
    <ng-content></ng-content>
  </mat-sidenav-content>
</mat-sidenav-container>
```

### layout/sidebar/sidebar.component.scss
```scss
.sidebar-container {
  height: 100vh;
}

.sidebar {
  width: 250px;
  background-color: #f5f5f5;
  
  .sidebar-header {
    padding: 1rem;
    border-bottom: 1px solid #e0e0e0;
    
    h3 {
      margin: 0;
      font-size: 1.25rem;
      font-weight: 500;
    }
  }
  
  mat-nav-list {
    padding-top: 1rem;
    
    a {
      cursor: pointer;
      transition: background-color 0.2s;
      
      &:hover {
        background-color: #e0e0e0;
      }
      
      &.active {
        background-color: #e3f2fd;
        color: #1976d2;
      }
    }
  }
}
```

---

## 4. FOOTER

### layout/footer/footer.component.ts
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
  currentYear: number = new Date().getFullYear();
}
```

### layout/footer/footer.component.html
```html
<footer class="footer">
  <div class="footer-content">
    <p>&copy; {{ currentYear }} Tecsup World Cup Challenge. All rights reserved.</p>
    <div class="footer-links">
      <a href="#">Terms</a>
      <a href="#">Privacy</a>
      <a href="#">Contact</a>
    </div>
  </div>
</footer>
```

### layout/footer/footer.component.scss
```scss
.footer {
  background-color: #263238;
  color: white;
  padding: 2rem 1rem;
  margin-top: auto;
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
    
    p {
      margin: 0;
      font-size: 0.875rem;
    }
    
    .footer-links {
      display: flex;
      gap: 1rem;
      
      a {
        color: white;
        text-decoration: none;
        font-size: 0.875rem;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
```

---

## 5. DASHBOARD LAYOUT

### layout/dashboard-layout/dashboard-layout.component.ts
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-layout',
  templateUrl: './dashboard-layout.component.html',
  styleUrls: ['./dashboard-layout.component.scss']
})
export class DashboardLayoutComponent {}
```

### layout/dashboard-layout/dashboard-layout.component.html
```html
<div class="dashboard-layout">
  <app-navbar></app-navbar>
  
  <div class="dashboard-content">
    <app-sidebar>
      <router-outlet></router-outlet>
    </app-sidebar>
  </div>
  
  <app-footer></app-footer>
</div>
```

### layout/dashboard-layout/dashboard-layout.component.scss
```scss
.dashboard-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  
  .dashboard-content {
    flex: 1;
    display: flex;
  }
}
```

---

## 6. PUBLIC LAYOUT

### layout/public-layout/public-layout.component.ts
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-public-layout',
  templateUrl: './public-layout.component.html',
  styleUrls: ['./public-layout.component.scss']
})
export class PublicLayoutComponent {}
```

### layout/public-layout/public-layout.component.html
```html
<div class="public-layout">
  <div class="public-header">
    <div class="logo">
      <h1>Tecsup World Cup 2026</h1>
    </div>
  </div>
  
  <div class="public-content">
    <router-outlet></router-outlet>
  </div>
  
  <app-footer></app-footer>
</div>
```

### layout/public-layout/public-layout.component.scss
```scss
.public-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .public-header {
    padding: 2rem;
    text-align: center;
    
    .logo {
      h1 {
        color: white;
        margin: 0;
        font-size: 2rem;
        font-weight: 700;
      }
    }
  }
  
  .public-content {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 2rem;
  }
}
```

---

## 7. LAYOUT MODULE

### layout/layout.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { SharedModule } from '../shared/shared.module';

// Components
import { NavbarComponent } from './navbar/navbar.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { FooterComponent } from './footer/footer.component';
import { DashboardLayoutComponent } from './dashboard-layout/dashboard-layout.component';
import { PublicLayoutComponent } from './public-layout/public-layout.component';

@NgModule({
  declarations: [
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    DashboardLayoutComponent,
    PublicLayoutComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatIconModule,
    MatMenuModule,
    SharedModule
  ],
  exports: [
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    DashboardLayoutComponent,
    PublicLayoutComponent
  ]
})
export class LayoutModule { }
```

---

## 8. ACTUALIZACIÓN DE APP.MODULE

### app/app.module.ts
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './shared/material/material.module';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { LayoutModule } from './layout/layout.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MaterialModule,
    CoreModule.forRoot(),
    SharedModule,
    LayoutModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

---

## TAREA SIGUIENTE

Una vez implementados los layouts, procede al README-05-auth.md para implementar el feature de autenticación.
