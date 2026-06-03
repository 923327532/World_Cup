# README 07: Matches y Predictions - Features de Partidos y Predicciones

## PROMPT PARA EL EQUIPO DE DESARROLLO FRONTEND

Como desarrollador frontend, tu tarea es implementar los features de partidos y predicciones que permitirán a los usuarios ver los partidos y realizar sus predicciones.

---

## 1. ESTRUCTURA DEL FEATURE MATCHES

```
features/matches
│
├── match-list
│   ├── match-list.component.ts
│   ├── match-list.component.html
│   └── match-list.component.scss
│
├── match-detail
│   ├── match-detail.component.ts
│   ├── match-detail.component.html
│   └── match-detail.component.scss
│
├── live-match
│   ├── live-match.component.ts
│   ├── live-match.component.html
│   └── live-match.component.scss
│
├── matches-routing.module.ts
└── matches.module.ts
```

---

## 2. ESTRUCTURA DEL FEATURE PREDICTIONS

```
features/predictions
│
├── create-prediction
│   ├── create-prediction.component.ts
│   ├── create-prediction.component.html
│   └── create-prediction.component.scss
│
├── prediction-history
│   ├── prediction-history.component.ts
│   ├── prediction-history.component.html
│   └── prediction-history.component.scss
│
├── predictions-routing.module.ts
└── predictions.module.ts
```

---

## 3. MATCH LIST COMPONENT

### features/matches/match-list/match-list.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

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
  selector: 'app-match-list',
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.scss']
})
export class MatchListComponent implements OnInit {
  
  matches: Match[] = [];
  loading = true;
  selectedDate: Date = new Date();
  
  constructor(private http: HttpClient) {}
  
  ngOnInit(): void {
    this.loadMatches();
  }
  
  loadMatches(): void {
    const dateStr = this.selectedDate.toISOString().split('T')[0];
    
    this.http.get<Match[]>(`${environment.apiUrl}/matches/date/${dateStr}`).subscribe({
      next: (matches) => {
        this.matches = matches;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
  
  onDateChange(date: Date): void {
    this.selectedDate = date;
    this.loadMatches();
  }
  
  onMatchClick(matchId: number): void {
    // Navigate to match detail
  }
}
```

### features/matches/match-list/match-list.component.html
```html
<div class="match-list-page">
  <div class="page-header">
    <h1>Matches</h1>
    <mat-form-field appearance="outline">
      <mat-label>Select Date</mat-label>
      <input matInput [matDatepicker]="picker" (dateChange)="onDateChange($event)" />
      <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
      <mat-datepicker #picker></mat-datepicker>
    </mat-form-field>
  </div>
  
  <div class="matches-grid" *ngIf="!loading">
    <app-match-card 
      *ngFor="let match of matches" 
      [match]="match"
      [clickable]="true"
      (click)="onMatchClick(match.id)">
    </app-match-card>
    
    <div class="no-matches" *ngIf="matches.length === 0">
      <p>No matches scheduled for this date</p>
    </div>
  </div>
  
  <div class="loading-container" *ngIf="loading">
    <mat-spinner diameter="50"></mat-spinner>
  </div>
</div>
```

### features/matches/match-list/match-list.component.scss
```scss
.match-list-page {
  padding: 2rem;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    
    h1 {
      margin: 0;
      font-size: 2rem;
      font-weight: 700;
    }
  }
  
  .matches-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1.5rem;
  }
  
  .no-matches {
    text-align: center;
    padding: 4rem;
    color: #666;
    font-size: 1.125rem;
  }
  
  .loading-container {
    display: flex;
    justify-content: center;
    padding: 4rem;
  }
}
```

---

## 4. LIVE MATCH COMPONENT

### features/matches/live-match/live-match.component.ts
```typescript
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from '../../../core/services/websocket.service';
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

interface Comment {
  id: number;
  userId: number;
  content: string;
  createdAt: string;
}

@Component({
  selector: 'app-live-match',
  templateUrl: './live-match.component.html',
  styleUrls: ['./live-match.component.scss']
})
export class LiveMatchComponent implements OnInit, OnDestroy {
  
  match: Match | null = null;
  comments: Comment[] = [];
  loading = true;
  newComment = '';
  
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private webSocketService: WebSocketService
  ) {}
  
  ngOnInit(): void {
    const matchId = +this.route.snapshot.paramMap.get('id')!;
    this.loadMatch(matchId);
    this.loadComments(matchId);
    this.webSocketService.connect();
    
    // Subscribe to live chat
    this.webSocketService.subscribeToMatchChat(matchId).subscribe(comment => {
      this.comments.push(comment);
    });
  }
  
  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }
  
  loadMatch(matchId: number): void {
    this.http.get<Match>(`${environment.apiUrl}/matches/${matchId}`).subscribe({
      next: (match) => {
        this.match = match;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
  
  loadComments(matchId: number): void {
    this.http.get<Comment[]>(`${environment.apiUrl}/comments/match/${matchId}`).subscribe({
      next: (comments) => {
        this.comments = comments;
      }
    });
  }
  
  sendComment(): void {
    if (!this.newComment.trim() || !this.match) return;
    
    this.webSocketService.sendMatchComment(this.match.id, {
      userId: 1, // Get from auth service
      content: this.newComment
    });
    
    this.newComment = '';
  }
}
```

### features/matches/live-match/live-match.component.html
```html
<div class="live-match-page" *ngIf="!loading && match">
  <div class="match-header">
    <h1>{{ match.homeTeam }} vs {{ match.awayTeam }}</h1>
    <div class="match-status" [class.live]="match.status === 'LIVE'">
      <span *ngIf="match.status === 'LIVE'" class="live-indicator">● LIVE</span>
      <span>{{ match.status }}</span>
    </div>
  </div>
  
  <div class="match-content">
    <div class="score-board">
      <div class="team home-team">
        <h2>{{ match.homeTeam }}</h2>
        <div class="score">{{ match.homeScore }}</div>
      </div>
      
      <div class="match-info">
        <div class="time">LIVE</div>
      </div>
      
      <div class="team away-team">
        <h2>{{ match.awayTeam }}</h2>
        <div class="score">{{ match.awayScore }}</div>
      </div>
    </div>
    
    <div class="live-chat">
      <div class="chat-header">
        <h3>Live Chat</h3>
      </div>
      
      <div class="chat-messages">
        <div class="chat-message" *ngFor="let comment of comments">
          <div class="message-user">User {{ comment.userId }}:</div>
          <div class="message-content">{{ comment.content }}</div>
        </div>
      </div>
      
      <div class="chat-input">
        <mat-form-field appearance="outline" class="full-width">
          <input matInput [(ngModel)]="newComment" placeholder="Type your message..." />
          <button mat-icon-button matSuffix (click)="sendComment()">
            <mat-icon>send</mat-icon>
          </button>
        </mat-form-field>
      </div>
    </div>
  </div>
</div>
```

### features/matches/live-match/live-match.component.scss
```scss
.live-match-page {
  padding: 2rem;
  
  .match-header {
    text-align: center;
    margin-bottom: 2rem;
    
    h1 {
      margin: 0 0 0.5rem;
      font-size: 2rem;
      font-weight: 700;
    }
    
    .match-status {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      background-color: #f5f5f5;
      border-radius: 20px;
      font-weight: 500;
      
      &.live {
        background-color: #ffebee;
        color: #f44336;
      }
      
      .live-indicator {
        animation: pulse 1.5s infinite;
      }
    }
  }
  
  .match-content {
    display: grid;
    grid-template-columns: 1fr 400px;
    gap: 2rem;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }
  
  .score-board {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 2rem;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    color: white;
    
    .team {
      text-align: center;
      
      h2 {
        margin: 0 0 1rem;
        font-size: 1.5rem;
      }
      
      .score {
        font-size: 4rem;
        font-weight: 700;
      }
    }
    
    .match-info {
      text-align: center;
      
      .time {
        font-size: 1.5rem;
        font-weight: 600;
      }
    }
  }
  
  .live-chat {
    background-color: #f5f5f5;
    border-radius: 16px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: 500px;
    
    .chat-header {
      padding: 1rem;
      background-color: white;
      border-bottom: 1px solid #e0e0e0;
      
      h3 {
        margin: 0;
        font-size: 1.125rem;
      }
    }
    
    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 1rem;
      
      .chat-message {
        margin-bottom: 1rem;
        
        .message-user {
          font-weight: 600;
          color: #1976d2;
          font-size: 0.875rem;
        }
        
        .message-content {
          background-color: white;
          padding: 0.75rem;
          border-radius: 8px;
          margin-top: 0.25rem;
        }
      }
    }
    
    .chat-input {
      padding: 1rem;
      background-color: white;
      border-top: 1px solid #e0e0e0;
      
      .full-width {
        width: 100%;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
```

---

## 5. CREATE PREDICTION COMPONENT

### features/predictions/create-prediction/create-prediction.component.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';

interface PredictionType {
  id: number;
  code: string;
  name: string;
  points: number;
}

@Component({
  selector: 'app-create-prediction',
  templateUrl: './create-prediction.component.html',
  styleUrls: ['./create-prediction.component.scss']
})
export class CreatePredictionComponent implements OnInit {
  
  predictionForm: FormGroup;
  predictionTypes: PredictionType[] = [];
  loading = false;
  matchId: number;
  
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {
    this.matchId = +this.route.snapshot.paramMap.get('matchId')!;
    
    this.predictionForm = this.fb.group({
      predictionTypeId: ['', Validators.required],
      predictionValue: ['', Validators.required]
    });
  }
  
  ngOnInit(): void {
    this.loadPredictionTypes();
  }
  
  loadPredictionTypes(): void {
    this.http.get<PredictionType[]>(`${environment.apiUrl}/predictions/types`).subscribe({
      next: (types) => {
        this.predictionTypes = types;
      }
    });
  }
  
  onSubmit(): void {
    if (this.predictionForm.invalid) return;
    
    this.loading = true;
    
    const { predictionTypeId, predictionValue } = this.predictionForm.value;
    
    this.http.post(`${environment.apiUrl}/predictions`, {
      matchId: this.matchId,
      predictionTypeId,
      predictionValue
    }).subscribe({
      next: () => {
        this.snackBar.open('Prediction created successfully!', 'Close', { duration: 3000 });
        this.router.navigate(['/predictions/history']);
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Failed to create prediction. Please try again.', 'Close', { duration: 5000 });
      }
    });
  }
}
```

### features/predictions/create-prediction/create-prediction.component.html
```html
<div class="create-prediction-page">
  <div class="page-header">
    <h1>Create Prediction</h1>
    <button mat-button (click)="router.navigate(['/matches'])">
      <mat-icon>arrow_back</mat-icon>
      Back to Matches
    </button>
  </div>
  
  <mat-card class="prediction-card">
    <mat-card-content>
      <form [formGroup]="predictionForm" (ngSubmit)="onSubmit()">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Prediction Type</mat-label>
          <mat-select formControlName="predictionTypeId">
            <mat-option *ngFor="let type of predictionTypes" [value]="type.id">
              {{ type.name }} ({{ type.points }} pts)
            </mat-option>
          </mat-select>
        </mat-form-field>
        
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Your Prediction</mat-label>
          <input matInput formControlName="predictionValue" placeholder="Enter your prediction" />
        </mat-form-field>
        
        <button mat-raised-button color="primary" type="submit" class="full-width" [disabled]="loading">
          <span *ngIf="!loading">Submit Prediction</span>
          <span *ngIf="loading">Submitting...</span>
        </button>
      </form>
    </mat-card-content>
  </mat-card>
</div>
```

### features/predictions/create-prediction/create-prediction.component.scss
```scss
.create-prediction-page {
  padding: 2rem;
  max-width: 600px;
  margin: 0 auto;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    
    h1 {
      margin: 0;
      font-size: 2rem;
      font-weight: 700;
    }
  }
  
  .prediction-card {
    .full-width {
      width: 100%;
      margin-bottom: 1rem;
    }
  }
}
```

---

## 6. MATCHES MODULE

### features/matches/matches.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchesRoutingModule } from './matches-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { LayoutModule } from '../../layout/layout.module';

// Components
import { MatchListComponent } from './match-list/match-list.component';
import { LiveMatchComponent } from './live-match/live-match.component';

@NgModule({
  declarations: [
    MatchListComponent,
    LiveMatchComponent
  ],
  imports: [
    CommonModule,
    MatchesRoutingModule,
    SharedModule,
    LayoutModule
  ]
})
export class MatchesModule { }
```

---

## 7. PREDICTIONS MODULE

### features/predictions/predictions.module.ts
```typescript
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { PredictionsRoutingModule } from './predictions-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { LayoutModule } from '../../layout/layout.module';

// Components
import { CreatePredictionComponent } from './create-prediction/create-prediction.component';
import { PredictionHistoryComponent } from './prediction-history/prediction-history.component';

@NgModule({
  declarations: [
    CreatePredictionComponent,
    PredictionHistoryComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    PredictionsRoutingModule,
    SharedModule,
    LayoutModule
  ]
})
export class PredictionsModule { }
```

---

## TAREA SIGUIENTE

Una vez implementados los features de partidos y predicciones, procede al README-08-rankings.md para implementar el feature de rankings.
