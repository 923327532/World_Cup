import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { Campus, Career, Department } from '../../models/organization.model';

@Injectable({ providedIn: 'root' })
export class OrganizationApiService {
  constructor(private http: HttpClient) {}

  getCampuses(): Observable<Campus[]> {
    return this.http.get<Campus[]>(API_ENDPOINTS.campuses, { context: this.silentContext() }).pipe(catchError(() => of([{ id: 1, name: 'Lima' }, { id: 2, name: 'Arequipa' }, { id: 3, name: 'Trujillo' }])));
  }

  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(API_ENDPOINTS.departments, { context: this.silentContext() }).pipe(catchError(() => of([{ id: 1, name: 'Tecnologia' }, { id: 2, name: 'Operaciones' }, { id: 3, name: 'Administracion' }])));
  }

  getCareersByDepartment(departmentId: number): Observable<Career[]> {
    return this.http.get<Career[]>(`${API_ENDPOINTS.careers}/department/${departmentId}`, { context: this.silentContext() }).pipe(catchError(() => of([
      { id: 11, name: 'Software', departmentId },
      { id: 12, name: 'Industrial', departmentId },
      { id: 13, name: 'Mecatronica', departmentId }
    ])));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
