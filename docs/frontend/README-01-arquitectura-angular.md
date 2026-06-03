# README 01: Arquitectura Angular - Estructura General

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es comprender e implementar la arquitectura general de Angular para el sistema Tecsup World Cup Challenge 2026.

---

## 1. ESTRUCTURA GENERAL DEL PROYECTO

```
world-cup-frontend
│
├── src
│
├── public
│
├── docs
│
├── docker
│
├── nginx
│
├── environments
│
├── angular.json
│
├── package.json
│
└── README.md
```

---

## 2. DENTRO DE SRC

```
src
│
├── app
│
├── assets
│
├── environments
│
├── styles
│
└── main.ts
```

---

## 3. ARQUITECTURA ANGULAR

### app
```
app
│
├── core
├── shared
├── layout
├── features
├── pages
├── guards
├── interceptors
├── services
├── models
└── store
```

---

## 4. STACK TECNOLÓGICO

### Framework
- **Angular 20** (última versión estable)

### UI Library
- **Angular Material** - Componentes de UI
- **@angular/cdk** - Componentes adicionales

### State Management
- **NgRx** - Gestión de estado global (opcional pero recomendado)
- **RxJS** - Programación reactiva

### HTTP Client
- **HttpClient** - Para llamadas API
- **WebSocket** - Para actualizaciones en tiempo real

### Build Tool
- **Angular CLI** - Para scaffolding y build

### Testing
- **Jasmine** - Unit testing
- **Karma** - Test runner
- **Protractor** - E2E testing (o Cypress)

### Additional
- **RxJS** - Streams reactivos
- **Moment.js o date-fns** - Manejo de fechas
- **Chart.js o ngx-charts** - Gráficos para estadísticas

---

## 5. DEPENDENCIAS (package.json)

```json
{
  "dependencies": {
    "@angular/animations": "^20.0.0",
    "@angular/cdk": "^20.0.0",
    "@angular/common": "^20.0.0",
    "@angular/compiler": "^20.0.0",
    "@angular/core": "^20.0.0",
    "@angular/forms": "^20.0.0",
    "@angular/material": "^20.0.0",
    "@angular/platform-browser": "^20.0.0",
    "@angular/platform-browser-dynamic": "^20.0.0",
    "@angular/router": "^20.0.0",
    "@ngrx/effects": "^18.0.0",
    "@ngrx/store": "^18.0.0",
    "@ngrx/store-devtools": "^18.0.0",
    "rxjs": "^7.8.0",
    "sockjs-client": "^1.6.0",
    "stompjs": "^2.3.3",
    "ngx-charts": "^20.0.0",
    "moment": "^2.29.4",
    "tslib": "^2.6.0",
    "zone.js": "^0.14.0"
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^20.0.0",
    "@angular/cli": "^20.0.0",
    "@angular/compiler-cli": "^20.0.0",
    "@types/jasmine": "^5.1.0",
    "@types/node": "^20.0.0",
    "jasmine-core": "^5.1.0",
    "karma": "^6.4.0",
    "typescript": "^5.3.0"
  }
}
```

---

## 6. CONFIGURACIÓN DE ANGULAR

### angular.json (configuración principal)
```json
{
  "$schema": "./node_modules/@angular/cli/lib/config/schema.json",
  "version": 2,
  "newProjectRoot": "projects",
  "projects": {
    "world-cup-frontend": {
      "projectType": "application",
      "schematics": {
        "@schematics/angular:component": {
          "style": "scss"
        }
      },
      "root": "",
      "sourceRoot": "src",
      "prefix": "app",
      "architect": {
        "build": {
          "builder": "@angular-devkit/build-angular:browser",
          "options": {
            "outputPath": "dist/world-cup-frontend",
            "index": "src/index.html",
            "main": "src/main.ts",
            "polyfills": [
              "zone.js"
            ],
            "tsConfig": "tsconfig.app.json",
            "assets": [
              "src/favicon.ico",
              "src/assets"
            ],
            "styles": [
              "src/styles.scss",
              "node_modules/@angular/material/prebuilt-themes/indigo-pink.css"
            ],
            "scripts": []
          }
        },
        "serve": {
          "builder": "@angular-devkit/build-angular:dev-server",
          "options": {
            "browserTarget": "world-cup-frontend:build"
          }
        }
      }
    }
  }
}
```

---

## 7. ENVIRONMENTS

### environments/environment.ts (desarrollo)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'http://localhost:8087/ws'
};
```

### environments/environment.prod.ts (producción)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.tecsup-worldcup.com/api',
  wsUrl: 'https://api.tecsup-worldcup.com/ws'
};
```

---

## 8. ROUTING

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
    path: 'matches',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/matches/matches.module').then(m => m.MatchesModule)
  },
  {
    path: 'predictions',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/predictions/predictions.module').then(m => m.PredictionsModule)
  },
  {
    path: 'rankings',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/rankings/rankings.module').then(m => m.RankingsModule)
  },
  {
    path: 'profile',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/profile/profile.module').then(m => m.ProfileModule)
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

---

## 9. ESTILOS GLOBALES

### styles.scss
```scss
@import '~@angular/material/theming';

// Custom theme
$custom-primary: mat-palette($mat-indigo);
$custom-accent: mat-palette($mat-pink, A200, A100, A400);

$custom-theme: mat-light-theme((
  color: (
    primary: $custom-primary,
    accent: $custom-accent
  )
));

@include angular-material-theme($custom-theme);

// Global styles
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Roboto', sans-serif;
  background-color: #f5f5f5;
}

// Custom utility classes
.text-center {
  text-align: center;
}

.mt-2 {
  margin-top: 1rem;
}

.mb-2 {
  margin-bottom: 1rem;
}

.p-2 {
  padding: 1rem;
}
```

---

## 10. MAIN.TS

### src/main.ts
```typescript
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
```

---

## 11. APP.MODULE

### app/app.module.ts
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './shared/material/material.module';
import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    MaterialModule,
    CoreModule,
    SharedModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

---

## 12. APP.COMPONENT

### app/app.component.ts
```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Tecsup World Cup Challenge 2026';
}
```

### app/app.component.html
```html
<router-outlet></router-outlet>
```

---

## 13. ESTRUCTURA DE MÓDULOS

### Lazy Loading
Cada feature será un módulo independiente con lazy loading para mejorar el rendimiento:

```typescript
{
  path: 'predictions',
  canActivate: [AuthGuard],
  loadChildren: () => import('./features/predictions/predictions.module').then(m => m.PredictionsModule)
}
```

---

## 14. BEST PRACTICES

### Componentes
- Usar **Standalone Components** (Angular 14+)
- Separar lógica de presentación
- Usar **OnPush** change detection
- Implementar **Unsubscribe** en ngOnDestroy

### Servicios
- Usar **Singleton pattern** con providedIn: 'root'
- Implementar **RxJS operators** para manejo de streams
- Usar **tap** para side effects
- Implementar **error handling** con catchError

### RxJS
- Usar **async pipe** en templates
- Implementar **takeUntil** para evitar memory leaks
- Usar **shareReplay** para caché de observables

### TypeScript
- Usar **strict mode** en tsconfig
- Definir **interfaces** para modelos
- Usar **enums** para constantes
- Implementar **type guards** cuando sea necesario

---

## 15. COMANDOS ÚTILES

### Crear nuevo componente
```bash
ng generate component components/my-component --module=shared
```

### Crear nuevo servicio
```bash
ng generate service services/my-service
```

### Crear nuevo módulo
```bash
ng generate module features/my-feature --routing
```

### Build para producción
```bash
ng build --configuration production
```

### Run development server
```bash
ng serve
```

---

## TAREA SIGUIENTE

Una vez comprendida la arquitectura general, procede al README-02-core.md para implementar los módulos core (guards, interceptors, services).
