import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminApiService } from '../../../services/api/admin-api.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ManualMatchResponse, ManualMatchRequest } from '../../../models/admin.model';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Component({
  selector: 'app-admin-matches',
  templateUrl: './admin-matches.component.html',
  styleUrls: ['./admin-matches.component.scss'],
  standalone: false,
})
export class AdminMatchesComponent implements OnInit {
  matches$: Observable<ManualMatchResponse[]> = of([]);
  matchForm: FormGroup;
  showForm = false;
  editingMatch: ManualMatchResponse | null = null;
  selectedStatus = 'all';
  isLoading = false;

  statusOptions = [
    { value: 'all', label: 'Todos', color: '#1a237e' },
    { value: 'SCHEDULED', label: 'Programados', color: '#ff9800' },
    { value: 'LIVE', label: 'En Vivo', color: '#f44336' },
    { value: 'FINISHED', label: 'Finalizados', color: '#4caf50' },
  ];

  constructor(
    private fb: FormBuilder,
    private adminApi: AdminApiService,
    private notification: NotificationService,
  ) {
    this.matchForm = this.fb.group({
      homeTeam: ['', Validators.required],
      awayTeam: ['', Validators.required],
      startTime: ['', Validators.required],
      venue: [''],
      stage: [''],
      groupName: [''],
    });
  }

  ngOnInit(): void {
    this.loadMatches();
  }

  loadMatches(): void {
    this.isLoading = true;
    if (this.selectedStatus === 'all') {
      this.matches$ = this.adminApi.getMatches().pipe(
        tap(() => (this.isLoading = false)),
        catchError(() => {
          this.isLoading = false;
          return of([]);
        }),
      );
    } else {
      this.matches$ = this.adminApi.getMatchesByStatus(this.selectedStatus).pipe(
        tap(() => (this.isLoading = false)),
        catchError(() => {
          this.isLoading = false;
          return of([]);
        }),
      );
    }
  }

  onStatusChange(status: string): void {
    this.selectedStatus = status;
    this.loadMatches();
  }

  openCreateForm(): void {
    this.editingMatch = null;
    this.matchForm.reset();
    this.showForm = true;
  }

  openEditForm(match: ManualMatchResponse): void {
    this.editingMatch = match;
    this.matchForm.patchValue({
      homeTeam: match.homeTeam,
      awayTeam: match.awayTeam,
      startTime: match.startTime,
      venue: match.venue,
      stage: match.stage,
      groupName: match.groupName,
    });
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingMatch = null;
    this.matchForm.reset();
  }

  onSubmit(): void {
    if (this.matchForm.invalid) return;

    const matchData: ManualMatchRequest = this.matchForm.value;

    if (this.editingMatch) {
      this.adminApi.updateMatch(this.editingMatch.id, matchData).subscribe({
        next: () => {
          this.notification.success('Partido actualizado correctamente');
          this.closeForm();
          this.loadMatches();
        },
        error: () => this.notification.error('Error al actualizar el partido'),
      });
    } else {
      this.adminApi.createMatch(matchData).subscribe({
        next: () => {
          this.notification.success('Partido creado correctamente');
          this.closeForm();
          this.loadMatches();
        },
        error: () => this.notification.error('Error al crear el partido'),
      });
    }
  }

  updateResult(match: ManualMatchResponse): void {
    const homeScore = prompt(`Goles de ${match.homeTeam}:`);
    const awayScore = prompt(`Goles de ${match.awayTeam}:`);

    if (homeScore === null || awayScore === null) return;

    const hScore = parseInt(homeScore, 10);
    const aScore = parseInt(awayScore, 10);

    if (isNaN(hScore) || isNaN(aScore)) {
      this.notification.error('Scores inválidos');
      return;
    }

    const winner = hScore > aScore ? match.homeTeam : aScore > hScore ? match.awayTeam : 'DRAW';

    this.adminApi
      .updateMatchResult(match.id, {
        homeScore: hScore,
        awayScore: aScore,
        winner,
      })
      .subscribe({
        next: () => {
          this.notification.success('Resultado actualizado');
          this.loadMatches();
        },
        error: () => this.notification.error('Error al actualizar resultado'),
      });
  }

  updateStatus(match: ManualMatchResponse, newStatus: string): void {
    this.adminApi.updateMatchStatus(match.id, newStatus).subscribe({
      next: () => {
        this.notification.success(`Estado cambiado a ${newStatus}`);
        this.loadMatches();
      },
      error: () => this.notification.error('Error al cambiar estado'),
    });
  }

  deleteMatch(match: ManualMatchResponse): void {
    if (!confirm(`¿Eliminar partido ${match.homeTeam} vs ${match.awayTeam}?`)) return;

    this.adminApi.deleteMatch(match.id).subscribe({
      next: () => {
        this.notification.success('Partido eliminado');
        this.loadMatches();
      },
      error: () => this.notification.error('Error al eliminar partido'),
    });
  }

  getStatusColor(status: string): string {
    const option = this.statusOptions.find((s) => s.value === status);
    return option?.color || '#64748b';
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'LIVE':
        return 'live_tv';
      case 'FINISHED':
        return 'check_circle';
      case 'SCHEDULED':
        return 'schedule';
      default:
        return 'sports_soccer';
    }
  }
}
