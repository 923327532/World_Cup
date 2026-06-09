import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import {
  AvatarDTO,
  Campus,
  Career,
  Department,
  StudentProfileDTO,
  TeacherProfileDTO,
} from '../../models/organization.model';

@Injectable({ providedIn: 'root' })
export class OrganizationApiService {
  constructor(private http: HttpClient) {}

  getCampuses(): Observable<Campus[]> {
    return this.http.get<Campus[]>(API_ENDPOINTS.campuses, { context: this.silentContext() }).pipe(
      catchError(() =>
        of([
          { id: 1, name: 'Lima' },
          { id: 2, name: 'Arequipa' },
          { id: 3, name: 'Trujillo' },
        ]),
      ),
    );
  }

  getCampusById(id: number): Observable<Campus> {
    return this.http
      .get<Campus>(`${API_ENDPOINTS.campuses}/${id}`, { context: this.silentContext() })
      .pipe(catchError(() => of({ id, name: 'Unknown' })));
  }

  getDepartments(): Observable<Department[]> {
    return this.http
      .get<Department[]>(API_ENDPOINTS.departments, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of([
            { id: 1, name: 'Tecnologia' },
            { id: 2, name: 'Operaciones' },
            { id: 3, name: 'Administracion' },
          ]),
        ),
      );
  }

  getDepartmentById(id: number): Observable<Department> {
    return this.http
      .get<Department>(`${API_ENDPOINTS.departments}/${id}`, { context: this.silentContext() })
      .pipe(catchError(() => of({ id, name: 'Unknown' })));
  }

  getCareers(): Observable<Career[]> {
    return this.http
      .get<Career[]>(API_ENDPOINTS.careers, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getCareersByDepartment(departmentId: number): Observable<Career[]> {
    return this.http
      .get<
        Career[]
      >(`${API_ENDPOINTS.careers}/department/${departmentId}`, { context: this.silentContext() })
      .pipe(
        catchError(() =>
          of([
            { id: 11, name: 'Software', departmentId },
            { id: 12, name: 'Industrial', departmentId },
            { id: 13, name: 'Mecatronica', departmentId },
          ]),
        ),
      );
  }

  createTeacherProfile(profile: TeacherProfileDTO): Observable<TeacherProfileDTO> {
    return this.http
      .post<TeacherProfileDTO>(API_ENDPOINTS.teacherProfiles, profile, {
        context: this.silentContext(),
      })
      .pipe(catchError(() => of(profile)));
  }

  getTeacherProfileByUser(userId: number): Observable<TeacherProfileDTO> {
    return this.http
      .get<TeacherProfileDTO>(`${API_ENDPOINTS.teacherProfiles}/user/${userId}`, {
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({ id: 0, userId, departmentId: undefined, specialization: '', bio: '' }),
        ),
      );
  }

  createStudentProfile(profile: StudentProfileDTO): Observable<StudentProfileDTO> {
    return this.http
      .post<StudentProfileDTO>(API_ENDPOINTS.studentProfiles, profile, {
        context: this.silentContext(),
      })
      .pipe(catchError(() => of(profile)));
  }

  getStudentProfileByUser(userId: number): Observable<StudentProfileDTO> {
    return this.http
      .get<StudentProfileDTO>(`${API_ENDPOINTS.studentProfiles}/user/${userId}`, {
        context: this.silentContext(),
      })
      .pipe(
        catchError(() =>
          of({
            id: 0,
            userId,
            campusId: undefined,
            careerId: undefined,
            studentCode: '',
            semester: 1,
          }),
        ),
      );
  }

  getAvatars(): Observable<AvatarDTO[]> {
    return this.http
      .get<AvatarDTO[]>(API_ENDPOINTS.avatars, { context: this.silentContext() })
      .pipe(catchError(() => of([])));
  }

  getAvatarById(id: number): Observable<AvatarDTO> {
    return this.http
      .get<AvatarDTO>(`${API_ENDPOINTS.avatars}/${id}`, { context: this.silentContext() })
      .pipe(catchError(() => of({ id, name: 'Default', url: '', isDefault: true })));
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
