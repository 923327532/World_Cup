import { Component, OnInit } from '@angular/core';
import { AdminApiService } from '../../../services/api/admin-api.service';
import { AdminAuditLogResponse } from '../../../models/admin.model';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-admin-audit',
  templateUrl: './admin-audit.component.html',
  styleUrls: ['./admin-audit.component.scss'],
  standalone: false,
})
export class AdminAuditComponent implements OnInit {
  auditLogs$: Observable<AdminAuditLogResponse[]> = of([]);
  isLoading = false;
  filterType = 'all';
  searchQuery = '';

  actionTypes = [
    { value: 'all', label: 'Todas', icon: 'filter_list', color: '#1a237e' },
    { value: 'CREATE', label: 'Creaciones', icon: 'add_circle', color: '#4caf50' },
    { value: 'UPDATE', label: 'Actualizaciones', icon: 'edit', color: '#2196f3' },
    { value: 'DELETE', label: 'Eliminaciones', icon: 'delete', color: '#f44336' },
  ];

  constructor(private adminApi: AdminApiService) {}

  ngOnInit(): void {
    this.loadAuditLogs();
  }

  loadAuditLogs(): void {
    this.isLoading = true;
    this.auditLogs$ = this.adminApi.getAuditLogs().pipe(
      catchError(() => {
        this.isLoading = false;
        return of([]);
      }),
    );
  }

  onFilterChange(type: string): void {
    this.filterType = type;
  }

  getFilteredLogs(logs: AdminAuditLogResponse[]): AdminAuditLogResponse[] {
    let filtered = logs;

    if (this.filterType !== 'all') {
      filtered = filtered.filter((log) => log.action.toUpperCase().includes(this.filterType));
    }

    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(
        (log) =>
          log.action.toLowerCase().includes(query) ||
          log.entityType.toLowerCase().includes(query) ||
          log.adminEmail.toLowerCase().includes(query) ||
          log.details.toLowerCase().includes(query),
      );
    }

    return filtered;
  }

  getActionIcon(action: string): string {
    const upperAction = action.toUpperCase();
    if (upperAction.includes('CREATE')) return 'add_circle';
    if (upperAction.includes('UPDATE')) return 'edit';
    if (upperAction.includes('DELETE')) return 'delete';
    return 'info';
  }

  getActionColor(action: string): string {
    const upperAction = action.toUpperCase();
    if (upperAction.includes('CREATE')) return '#4caf50';
    if (upperAction.includes('UPDATE')) return '#2196f3';
    if (upperAction.includes('DELETE')) return '#f44336';
    return '#64748b';
  }

  getActionGradient(action: string): string {
    const upperAction = action.toUpperCase();
    if (upperAction.includes('CREATE')) return 'linear-gradient(135deg, #4caf50, #8bc34a)';
    if (upperAction.includes('UPDATE')) return 'linear-gradient(135deg, #2196f3, #03a9f4)';
    if (upperAction.includes('DELETE')) return 'linear-gradient(135deg, #f44336, #e91e63)';
    return 'linear-gradient(135deg, #64748b, #94a3b8)';
  }

  getEntityIcon(entityType: string): string {
    const type = entityType.toLowerCase();
    if (type.includes('match')) return 'sports_soccer';
    if (type.includes('user')) return 'person';
    if (type.includes('team')) return 'groups';
    if (type.includes('prediction')) return 'edit_note';
    return 'description';
  }

  trackByLogId(index: number, log: AdminAuditLogResponse): number {
    return log.id;
  }
}
