# 🏆 World Cup Prediction Platform

<p align="center">
  <img src="https://img.shields.io/badge/Angular-22.0.0-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" />
  <img src="https://img.shields.io/badge/Angular_Material-22.0.0-3F51B5?style=for-the-badge&logo=angular&logoColor=white" alt="Material" />
  <img src="https://img.shields.io/badge/NgRx-21.1.0-BA2BD2?style=for-the-badge&logo=redux&logoColor=white" alt="NgRx" />
  <img src="https://img.shields.io/badge/TypeScript-6.0-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" />
  <img src="https://img.shields.io/badge/RxJS-7.8-B7178C?style=for-the-badge&logo=reactivex&logoColor=white" alt="RxJS" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat-square" alt="License" />
  <img src="https://img.shields.io/badge/Status-Active_Development-brightgreen?style=flat-square" alt="Status" />
  <img src="https://img.shields.io/badge/Platform-Web-blue?style=flat-square" alt="Platform" />
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [API Integration](#-api-integration)
- [Data Models](#-data-models)
- [State Management](#-state-management)
- [Real-time Features](#-real-time-features)
- [Contributing](#-contributing)

---

## 🌟 Overview

> A modern, real-time **World Cup Prediction Platform** built with Angular 22, featuring live match tracking, interactive predictions, gamification, social features, and comprehensive admin tools.

<p align="center">
  🎯 <strong>Predict</strong> match outcomes • 🏅 <strong>Compete</strong> on leaderboards • 💬 <strong>Engage</strong> with live chat • 🎮 <strong>Earn</strong> achievements
</p>

### What is this project?

This is a full-featured prediction platform designed for university communities to engage with World Cup matches. Users can:

- **Predict** match results, goal scorers, and other match events
- **Earn points** based on prediction accuracy
- **Compete** on global, campus, department, and career leaderboards
- **Unlock badges** and rewards through gamification
- **Chat live** during matches with real-time WebSocket communication
- **Track progress** through a personalized dashboard

The platform supports three user roles: **Student**, **Teacher**, and **Admin**, each with specific permissions and views.

---

## ✨ Features

### 🔐 Authentication Module

| Feature            | Description                                             |
| ------------------ | ------------------------------------------------------- |
| Login              | Email/password authentication with JWT tokens           |
| Register           | User registration with role selection (STUDENT/TEACHER) |
| Email Verification | Token-based email verification flow                     |
| Session Management | Persistent sessions via localStorage                    |
| Route Guards       | AuthGuard and AdminGuard for route protection           |

**Key Files:**

- `src/app/core/services/auth.service.ts` - Authentication logic
- `src/app/core/guards/auth.guard.ts` - Route protection
- `src/app/features/auth/` - Auth UI components

### 📊 Dashboard Module

| Feature               | Description                                        |
| --------------------- | -------------------------------------------------- |
| Active Matches Widget | Real-time display of ongoing matches               |
| Statistics Cards      | User points, prediction accuracy, ranking position |
| Quick Actions         | Fast navigation to predictions and live matches    |
| Responsive Layout     | Adaptive grid layout for all screen sizes          |

**Key Files:**

- `src/app/features/dashboard/dashboard-page/` - Main dashboard
- `src/app/features/dashboard/widgets/active-matches/` - Live matches widget

### ⚽ Matches Module

| Feature      | Description                                                      |
| ------------ | ---------------------------------------------------------------- |
| Match List   | Browse all matches with status filters (SCHEDULED/LIVE/FINISHED) |
| Match Detail | Detailed view with teams, scores, and kickoff times              |
| Live Match   | Real-time score updates and match events                         |
| Match Events | Goals, cards, and substitutions tracking                         |

**Match Statuses:**

- `SCHEDULED` - Upcoming matches
- `LIVE` - Currently in progress
- `FINISHED` - Completed matches

**Key Files:**

- `src/app/features/matches/match-list/` - Match listing
- `src/app/features/matches/match-detail/` - Match details
- `src/app/features/matches/live-match/` - Live match view

### 🎯 Predictions Module

| Feature            | Description                                           |
| ------------------ | ----------------------------------------------------- |
| Create Prediction  | Submit predictions for match outcomes                 |
| Prediction Types   | Multiple prediction categories (result, scorer, etc.) |
| Prediction History | View all past predictions with results                |
| Points System      | Automatic scoring based on accuracy                   |
| Lock Mechanism     | Predictions lock before match kickoff                 |

**Prediction Flow:**

1. Select a match
2. Choose prediction type
3. Submit prediction value
4. Earn points when match resolves

**Key Files:**

- `src/app/features/predictions/create-prediction/` - Create predictions
- `src/app/features/predictions/prediction-history/` - View history

### 🏆 Rankings Module

| Leaderboard | Scope                                |
| ----------- | ------------------------------------ |
| Global      | All users platform-wide              |
| Campus      | Users within the same campus         |
| Department  | Users within the same department     |
| Career      | Users within the same career/program |

**Ranking Data:**

- Position ranking
- Total points
- User profile (name, avatar)
- Campus/Department/Career affiliation

**Key Files:**

- `src/app/features/rankings/global-ranking/`
- `src/app/features/rankings/campus-ranking/`
- `src/app/features/rankings/department-ranking/`
- `src/app/features/rankings/career-ranking/`

### 🎮 Gamification Module

| Feature       | Description                                   |
| ------------- | --------------------------------------------- |
| Achievements  | Milestone-based accomplishments               |
| Badges        | Collectible badges with criteria and progress |
| Rewards       | Point-based reward redemption system          |
| Score History | Detailed log of points earned and reasons     |

**Badge System:**

- Each badge has criteria, points value, and icon
- Progress tracking for partially completed badges
- Earned badges display timestamp

**Key Files:**

- `src/app/features/gamification/achievements/`
- `src/app/features/gamification/badges/`
- `src/app/features/gamification/rewards/`

### 💬 Social Module

| Feature   | Description                                   |
| --------- | --------------------------------------------- |
| Live Chat | Real-time match-specific chat rooms           |
| Reactions | Emoji reactions to messages and events        |
| WebSocket | STOMP over SockJS for real-time communication |

**WebSocket Channels:**

- `/topic/chat/{matchId}` - Match chat subscriptions
- `/app/chat/{matchId}` - Send chat messages

**Key Files:**

- `src/app/features/social/live-chat/`
- `src/app/features/social/reactions/`
- `src/app/core/services/websocket.service.ts`

### 🛡️ Admin Module

| Feature             | Description                        |
| ------------------- | ---------------------------------- |
| Admin Dashboard     | Overview of platform statistics    |
| Match Management    | Create, update, and manage matches |
| Audit Logs          | Track administrative actions       |
| User Administration | Manage user accounts and roles     |

**Key Files:**

- `src/app/features/admin/admin-dashboard/`
- `src/app/features/admin/admin-matches/`
- `src/app/features/admin/admin-audit/`

---

## 🛠️ Tech Stack

<div align="center">

| Category                 | Technology           | Version |
| ------------------------ | -------------------- | ------- |
| **Framework**            | Angular              | 22.0.0  |
| **UI Components**        | Angular Material     | 22.0.0  |
| **Component Dev Kit**    | Angular CDK          | 22.0.0  |
| **State Management**     | NgRx Store           | 21.1.0  |
| **Side Effects**         | NgRx Effects         | 21.1.0  |
| **DevTools**             | NgRx Store DevTools  | 21.1.0  |
| **Charts**               | @swimlane/ngx-charts | 24.0.0  |
| **Date Handling**        | Moment.js            | 2.30.1  |
| **Reactive Programming** | RxJS                 | 7.8.0   |
| **WebSocket Client**     | SockJS               | 1.6.1   |
| **STOMP Protocol**       | stompjs              | 2.3.3   |
| **Language**             | TypeScript           | 6.0.2   |
| **Build System**         | Angular Build        | 22.0.0  |
| **Test Runner**          | Vitest               | 4.0.8   |
| **Code Formatter**       | Prettier             | 3.8.1   |
| **DOM Testing**          | jsdom                | 28.0.0  |

</div>

---

## 🏗️ Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          World Cup Frontend                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                         Layout Layer                              │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────────────────────┐  │   │
│  │  │  Navbar    │  │  Sidebar   │  │         Footer             │  │   │
│  │  │  Component │  │  Component │  │         Component          │  │   │
│  │  └────────────┘  └────────────┘  └────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        Feature Modules (Lazy-Loaded)              │   │
│  │  ┌─────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────┐  │   │
│  │  │  Auth   │ │Dashboard │ │ Matches  │ │Predictions│ │Rankings│  │   │
│  │  └─────────┘ └──────────┘ └──────────┘ └──────────┘ └────────┘  │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐                         │   │
│  │  │Gamification│ │ Social  │ │  Admin   │                         │   │
│  │  └──────────┘ └──────────┘ └──────────┘                         │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                         Core Module                               │   │
│  │  ┌────────────┐  ┌──────────────┐  ┌─────────────────────────┐   │   │
│  │  │   Guards   │  │ Interceptors │  │       Services          │   │   │
│  │  │            │  │              │  │                         │   │   │
│  │  │ • Auth     │  │ • Auth       │  │ • AuthService           │   │   │
│  │  │ • Admin    │  │ • Error      │  │ • WebsocketService      │   │   │
│  │  │            │  │ • Loading    │  │ • CacheService          │   │   │
│  │  │            │  │              │  │ • NotificationService   │   │   │
│  │  │            │  │              │  │ • StorageService        │   │   │
│  │  └────────────┘  └──────────────┘  │ • ErrorHandlerService   │   │   │
│  │                                    │ • LoadingService        │   │   │
│  │                                    └─────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                       API Service Layer                           │   │
│  │  AuthApi │ MatchApi │ PredictionApi │ RankingApi │ SocialApi     │   │
│  │  AdminApi │ GamificationApi │ GroupApi │ OrganizationApi         │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        Shared Module                              │   │
│  │  Components: MatchCard │ Avatar │ Loading │ AppBadge             │   │
│  │  Pipes: Date │ Points                                            │   │
│  │  Directives: ClickOutside                                        │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Routing Architecture

```
/                           → Redirect to /dashboard (requires auth)
/auth                       → Public layout (login, register, verify)
/dashboard                  → Dashboard module
/matches                    → Matches module (list, detail, live)
/predictions                → Predictions module (create, history)
/rankings                   → Rankings module (global, campus, dept, career)
/social                     → Social module (chat, reactions)
/gamification               → Gamification module (achievements, badges, rewards)
/admin                      → Admin module (dashboard, matches, audit)
```

---

## 🚀 Getting Started

### Prerequisites

| Tool        | Minimum Version | Recommended |
| ----------- | --------------- | ----------- |
| Node.js     | 20.x            | 22.x LTS    |
| npm         | 11.x            | Latest      |
| Angular CLI | 22.x            | Latest      |

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-org/world-cup-frontend.git

# 2. Navigate to project directory
cd world-cup-frontend

# 3. Install dependencies
npm install

# 4. Configure environment (optional)
# Edit src/environments/environment.ts with your API URLs
```

### Environment Configuration

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'http://localhost:8080/ws/chat',
};
```

| Variable | Description                               | Default                         |
| -------- | ----------------------------------------- | ------------------------------- |
| `apiUrl` | Backend REST API base URL                 | `http://localhost:8080/api`     |
| `wsUrl`  | WebSocket endpoint for real-time features | `http://localhost:8080/ws/chat` |

### Development Server

```bash
# Start development server
ng serve

# Start with specific port
ng serve --port 4201

# Start with live reload disabled
ng serve --live-reload=false
```

> 🌐 Open [http://localhost:4200](http://localhost:4200) in your browser.

### Building

```bash
# Development build
ng build

# Production build
ng build --configuration production

# Build with stats analysis
ng build --stats-json
```

Build artifacts are output to `dist/` directory.

### Testing

```bash
# Run unit tests
ng test

# Run tests in watch mode
ng test --watch

# Run tests with coverage
ng test --coverage
```

### Code Scaffolding

```bash
# Generate a component
ng generate component features/my-feature

# Generate a service
ng generate service services/my-service

# Generate a module
ng generate module features/my-feature

# Generate a guard
ng generate guard core/guards/my-guard

# Generate a pipe
ng generate pipe shared/pipes/my-pipe
```

---

## 📁 Project Structure

```
world-cup-frontend/
│
├── src/
│   ├── app/
│   │   ├── core/                          # Singleton services (providedIn: 'root')
│   │   │   ├── constants/
│   │   │   │   └── api.constants.ts       # API endpoint definitions & cache TTLs
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts          # Authentication route guard
│   │   │   │   └── admin.guard.ts         # Admin-only route guard
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts    # Attach JWT tokens to requests
│   │   │   │   ├── error.interceptor.ts   # Global error handling
│   │   │   │   └── loading.interceptor.ts # Loading state management
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts        # Authentication business logic
│   │   │   │   ├── cache.service.ts       # In-memory caching with TTL
│   │   │   │   ├── error-handler.service.ts
│   │   │   │   ├── loading.service.ts
│   │   │   │   ├── notification.service.ts
│   │   │   │   ├── storage.service.ts     # localStorage abstraction
│   │   │   │   └── websocket.service.ts   # STOMP WebSocket client
│   │   │   └── core.module.ts
│   │   │
│   │   ├── features/                      # Lazy-loaded feature modules
│   │   │   ├── auth/                      # /auth
│   │   │   │   ├── login/
│   │   │   │   ├── register/
│   │   │   │   ├── verify-email/
│   │   │   │   ├── auth-routing.module.ts
│   │   │   │   └── auth.module.ts
│   │   │   │
│   │   │   ├── dashboard/                 # /dashboard
│   │   │   │   ├── dashboard-page/
│   │   │   │   ├── widgets/
│   │   │   │   │   └── active-matches/
│   │   │   │   ├── dashboard-routing.module.ts
│   │   │   │   └── dashboard.module.ts
│   │   │   │
│   │   │   ├── matches/                   # /matches
│   │   │   │   ├── match-list/
│   │   │   │   ├── match-detail/
│   │   │   │   ├── live-match/
│   │   │   │   ├── matches-routing.module.ts
│   │   │   │   └── matches.module.ts
│   │   │   │
│   │   │   ├── predictions/               # /predictions
│   │   │   │   ├── create-prediction/
│   │   │   │   ├── prediction-history/
│   │   │   │   ├── predictions-routing.module.ts
│   │   │   │   └── predictions.module.ts
│   │   │   │
│   │   │   ├── rankings/                  # /rankings
│   │   │   │   ├── global-ranking/
│   │   │   │   ├── campus-ranking/
│   │   │   │   ├── department-ranking/
│   │   │   │   ├── career-ranking/
│   │   │   │   ├── rankings-routing.module.ts
│   │   │   │   └── rankings.module.ts
│   │   │   │
│   │   │   ├── gamification/              # /gamification
│   │   │   │   ├── achievements/
│   │   │   │   ├── badges/
│   │   │   │   ├── rewards/
│   │   │   │   ├── gamification-routing.module.ts
│   │   │   │   └── gamification.module.ts
│   │   │   │
│   │   │   ├── social/                    # /social
│   │   │   │   ├── live-chat/
│   │   │   │   ├── reactions/
│   │   │   │   ├── social-routing.module.ts
│   │   │   │   └── social.module.ts
│   │   │   │
│   │   │   └── admin/                     # /admin
│   │   │       ├── admin-dashboard/
│   │   │       ├── admin-matches/
│   │   │       ├── admin-audit/
│   │   │       ├── admin-routing.module.ts
│   │   │       └── admin.module.ts
│   │   │
│   │   ├── layout/                        # Shared layout components
│   │   │   ├── dashboard-layout/          # Authenticated layout
│   │   │   ├── public-layout/             # Public/auth layout
│   │   │   ├── navbar/
│   │   │   ├── sidebar/
│   │   │   ├── footer/
│   │   │   └── layout.module.ts
│   │   │
│   │   ├── models/                        # TypeScript interfaces
│   │   │   ├── user.model.ts              # User, AuthResponse
│   │   │   ├── match.model.ts             # Match, Team, MatchEvent
│   │   │   ├── prediction.model.ts        # PredictionDTO, PredictionTypeDTO
│   │   │   ├── ranking.model.ts           # LeaderboardEntry, Badge, Reward
│   │   │   ├── admin.model.ts
│   │   │   ├── group.model.ts
│   │   │   ├── organization.model.ts
│   │   │   └── worldcup.model.ts
│   │   │
│   │   ├── services/                      # API service layer
│   │   │   └── api/
│   │   │       ├── auth-api.service.ts
│   │   │       ├── match-api.service.ts
│   │   │       ├── prediction-api.service.ts
│   │   │       ├── ranking-api.service.ts
│   │   │       ├── social-api.service.ts
│   │   │       ├── admin-api.service.ts
│   │   │       ├── gamification-api.service.ts
│   │   │       ├── group-api.service.ts
│   │   │       ├── organization-api.service.ts
│   │   │       ├── notification-api.service.ts
│   │   │       ├── scoring-api.service.ts
│   │   │       ├── widget-proxy.service.ts
│   │   │       ├── worldcup-api.service.ts
│   │   │       └── mock-data.ts
│   │   │
│   │   ├── shared/                        # Shared module
│   │   │   ├── components/
│   │   │   │   ├── app-badge/
│   │   │   │   ├── avatar/
│   │   │   │   ├── loading/
│   │   │   │   └── match-card/
│   │   │   ├── directives/
│   │   │   │   └── click-outside.directive.ts
│   │   │   ├── pipes/
│   │   │   │   ├── date.pipe.ts
│   │   │   │   └── points.pipe.ts
│   │   │   ├── material/
│   │   │   │   └── material.module.ts     # Angular Material exports
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── app-routing.module.ts          # Root routing configuration
│   │   ├── app.component.ts               # Root component
│   │   ├── app.module.ts                  # Root module
│   │   └── main.ts                        # Application entry point
│   │
│   ├── environments/
│   │   ├── environment.ts                 # Development environment
│   │   └── environment.prod.ts            # Production environment
│   │
│   ├── types/
│   │   └── stomp-browser.d.ts             # STOMP type declarations
│   │
│   ├── index.html                         # HTML entry point
│   ├── main.ts                            # Bootstrap file
│   └── styles.scss                        # Global styles
│
├── public/
│   ├── images/
│   │   └── landing/                       # Landing page images
│   └── videos/                            # Intro and promotional videos
│
├── angular.json                           # Angular CLI configuration
├── package.json                           # Dependencies and scripts
├── tsconfig.json                          # TypeScript configuration
├── Dockerfile                             # Docker containerization
├── nginx.conf                             # Nginx configuration
└── README.md                              # This file
```

---

## 🔌 API Integration

### Endpoint Reference

The frontend communicates with the backend through typed API services. All endpoints are defined in `api.constants.ts`.

| Service                  | Base Endpoint        | Purpose                                  |
| ------------------------ | -------------------- | ---------------------------------------- |
| `AuthApiService`         | `/api/auth`          | Login, register, email verification      |
| `OrganizationApiService` | `/api/organization`  | Campuses, departments, careers, profiles |
| `WorldcupApiService`     | `/api/worldcup`      | Tournament data                          |
| `MatchApiService`        | `/api/matches`       | Match CRUD and status                    |
| `PredictionApiService`   | `/api/predictions`   | Create and manage predictions            |
| `RankingApiService`      | `/api/leaderboard`   | Leaderboard and rankings                 |
| `SocialApiService`       | `/api/social`        | Chat and social features                 |
| `GamificationApiService` | `/api/gamification`  | Badges and rewards                       |
| `ScoringApiService`      | `/api/scoring`       | Points and score history                 |
| `AdminApiService`        | `/api/admin`         | Administrative operations                |
| `GroupApiService`        | `/api/groups`        | Match groups                             |
| `NotificationApiService` | `/api/notifications` | User notifications                       |

### HTTP Interceptors

| Interceptor          | Function                                              |
| -------------------- | ----------------------------------------------------- |
| `AuthInterceptor`    | Attaches JWT Bearer token to all outgoing requests    |
| `ErrorInterceptor`   | Catches HTTP errors and routes to ErrorHandlerService |
| `LoadingInterceptor` | Manages loading state for HTTP requests               |

### Authentication Flow

```
┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│  Login   │────▶│  Auth API    │────▶│  JWT Token   │────▶│  Store   │
│  Form    │     │  /auth/login │     │  Received    │     │  in      │
│          │     │              │     │              │     │  Storage │
└──────────┘     └──────────────┘     └──────────────┘     └──────────┘
                                                               │
                                                               ▼
┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│  Access  │◀────│  Response    │◀────│  Token       │◀────│  Auth    │
│  Protected│    │  with Data   │     │  Attached    │     │  Header  │
│  Route   │     │              │     │  to Request  │     │  Bearer  │
└──────────┘     └──────────────┘     └──────────────┘     └──────────┘
```

### Caching Strategy

```typescript
// Cache TTL configuration (src/app/core/constants/api.constants.ts)
export const CACHE_TTL = {
  liveMatches: 15_000, // 15 seconds
  scheduledMatches: 3_600_000, // 1 hour
  standings: 300_000, // 5 minutes
  rankings: 300_000, // 5 minutes
};
```

---

## 📦 Data Models

### User Model

```typescript
interface User {
  userId: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
  studentCode?: string;
  avatarUrl?: string;
}
```

### Match Model

```typescript
type MatchStatus = 'SCHEDULED' | 'LIVE' | 'FINISHED';

interface Match {
  id: number;
  homeTeam: string;
  awayTeam: string;
  homeTeamId?: number;
  awayTeamId?: number;
  homeScore?: number;
  awayScore?: number;
  kickoffTime: string;
  status: MatchStatus;
}
```

### Prediction Model

```typescript
interface PredictionDTO {
  id: number;
  userId: number;
  matchId: number;
  homeTeam: string;
  awayTeam: string;
  predictionTypeId: number;
  predictionType: string;
  predictionValue: string;
  points: number;
  createdAt: string;
  updatedAt: string;
  isLocked: boolean;
}
```

### Ranking Model

```typescript
interface LeaderboardEntryDTO {
  rankingPosition: number;
  userId: number;
  email: string;
  firstName?: string;
  lastName?: string;
  points: number;
  campusName?: string;
  careerName?: string;
  departmentName?: string;
}
```

---

## 🔄 State Management

### NgRx Store

The application uses NgRx for global state management:

| Package                | Purpose                                |
| ---------------------- | -------------------------------------- |
| `@ngrx/store`          | Reactive state container               |
| `@ngrx/effects`        | Side effect handling (API calls, etc.) |
| `@ngrx/store-devtools` | Redux DevTools integration             |

### BehaviorSubject Services

For simpler state needs, the app uses RxJS `BehaviorSubject`:

| Service                             | State Managed               |
| ----------------------------------- | --------------------------- |
| `AuthService.user$`                 | Current authenticated user  |
| `WebsocketService.connectionState$` | WebSocket connection status |

---

## 📡 Real-time Features

### WebSocket Architecture

The platform uses **STOMP over SockJS** for real-time communication:

```
Browser                    Backend
   │                         │
   │──── SockJS connect ────▶│
   │                         │
   │◀─── Connection ack ─────│
   │                         │
   │──── STOMP subscribe ───▶│  /topic/chat/{matchId}
   │                         │
   │◀─── Message stream ─────│  Real-time chat messages
   │                         │
   │──── STOMP send ────────▶│  /app/chat/{matchId}
   │                         │
```

### WebSocket Service API

```typescript
// Connect to WebSocket server
connect(): void

// Disconnect from server
disconnect(): void

// Subscribe to match-specific chat
subscribeToMatchChat(matchId: number): Observable<ChatMessage>

// Send a chat message
sendMatchComment(matchId: number, comment: ChatMessage): void

// Subscribe to live score updates
subscribeToLiveScores(): Observable<ScoreUpdate>
```

### Live Score Updates

Score updates are polled every 15 seconds as fallback:

```typescript
interface ScoreUpdate {
  matchId: number;
  homeScore: number;
  awayScore: number;
  minute: number;
}
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

This project uses **Prettier** for code formatting. Run before committing:

```bash
npx prettier --write "src/**/*.{ts,html,scss}"
```

---

<p align="center">
  Made with ❤️ for the beautiful game ⚽
</p>
