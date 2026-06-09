import { Component, OnInit } from '@angular/core';
import { AdminApiService } from '../../../services/api/admin-api.service';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ManualMatchResponse, AdminAuditLogResponse } from '../../../models/admin.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss'],
  standalone: false,
})
export class AdminDashboardComponent implements OnInit {
  matches$: Observable<ManualMatchResponse[]> = of([]);
  auditLogs$: Observable<AdminAuditLogResponse[]> = of([]);
  stats = { totalMatches: 0, liveMatches: 0, finishedMatches: 0, scheduledMatches: 0 };

  constructor(private adminApi: AdminApiService) {}

  ngOnInit(): void {
    this.loadMatches();
    this.loadAuditLogs();
  }

  loadMatches(): void {
    this.matches$ = this.adminApi.getMatches().pipe(catchError(() => of([])));
    this.matches$.subscribe((matches) => {
      this.stats.totalMatches = matches.length;
      this.stats.liveMatches = matches.filter((m) => m.status === 'LIVE').length;
      this.stats.finishedMatches = matches.filter((m) => m.status === 'FINISHED').length;
      this.stats.scheduledMatches = matches.filter((m) => m.status === 'SCHEDULED').length;
    });
  }

  loadAuditLogs(): void {
    this.auditLogs$ = this.adminApi.getAuditLogs().pipe(catchError(() => of([])));
  }

  getAuditIcon(action: string): string {
    const upperAction = action.toUpperCase();
    if (upperAction.includes('CREATE')) return 'add_circle';
    if (upperAction.includes('UPDATE')) return 'edit';
    if (upperAction.includes('DELETE')) return 'delete';
    return 'info';
  }
}
