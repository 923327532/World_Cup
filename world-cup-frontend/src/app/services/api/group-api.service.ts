import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import {
  BanResponse,
  CreateGroupRequest,
  CreateInviteRequest,
  CreateReportRequest,
  InviteResponse,
  MemberResponse,
  ReportResponse,
  RoomResponse,
} from '../../models/group.model';

@Injectable({ providedIn: 'root' })
export class GroupApiService {
  constructor(private http: HttpClient) {}

  private userHeaders(): HttpHeaders {
    const userId = localStorage.getItem('userId') || '';
    return new HttpHeaders().set('X-User-Id', userId);
  }

  getGroups(): Observable<RoomResponse[]> {
    return this.http.get<RoomResponse[]>(API_ENDPOINTS.groups).pipe(catchError(() => of([])));
  }

  getGroupById(id: number): Observable<RoomResponse> {
    return this.http.get<RoomResponse>(`${API_ENDPOINTS.groups}/${id}`).pipe(
      catchError(() =>
        of({
          id,
          name: '',
          description: '',
          inviteCode: '',
          createdBy: 0,
          maxMembers: 0,
          status: '',
          createdAt: '',
          updatedAt: '',
        } as RoomResponse),
      ),
    );
  }

  createGroup(payload: CreateGroupRequest): Observable<RoomResponse> {
    return this.http
      .post<RoomResponse>(API_ENDPOINTS.groups, payload, { headers: this.userHeaders() })
      .pipe(
        catchError(() =>
          of({
            id: Date.now(),
            name: payload.name,
            description: payload.description ?? '',
            inviteCode: '',
            createdBy: 0,
            maxMembers: payload.maxMembers ?? 0,
            status: 'ACTIVE',
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          } as RoomResponse),
        ),
      );
  }

  updateGroup(id: number, payload: CreateGroupRequest): Observable<RoomResponse> {
    return this.http
      .put<RoomResponse>(`${API_ENDPOINTS.groups}/${id}`, payload, { headers: this.userHeaders() })
      .pipe(
        catchError(() =>
          of({
            id,
            name: payload.name,
            description: payload.description ?? '',
            inviteCode: '',
            createdBy: 0,
            maxMembers: payload.maxMembers ?? 0,
            status: 'ACTIVE',
            createdAt: '',
            updatedAt: '',
          } as RoomResponse),
        ),
      );
  }

  deleteGroup(id: number): Observable<void> {
    return this.http
      .delete<void>(`${API_ENDPOINTS.groups}/${id}`, { headers: this.userHeaders() })
      .pipe(catchError(() => of(void 0)));
  }

  joinGroup(inviteCode: string): Observable<RoomResponse> {
    return this.http
      .post<RoomResponse>(
        `${API_ENDPOINTS.groups}/join/${inviteCode}`,
        {},
        { headers: this.userHeaders() },
      )
      .pipe(
        catchError(() =>
          of({
            id: 0,
            name: '',
            description: '',
            inviteCode,
            createdBy: 0,
            maxMembers: 0,
            status: '',
            createdAt: '',
            updatedAt: '',
          } as RoomResponse),
        ),
      );
  }

  getGroupMembers(roomId: number): Observable<MemberResponse[]> {
    return this.http
      .get<MemberResponse[]>(`${API_ENDPOINTS.groups}/${roomId}/members`)
      .pipe(catchError(() => of([])));
  }

  removeMember(roomId: number, memberId: number): Observable<void> {
    return this.http
      .delete<void>(`${API_ENDPOINTS.groups}/${roomId}/members/${memberId}`, {
        headers: this.userHeaders(),
      })
      .pipe(catchError(() => of(void 0)));
  }

  updateMemberRole(roomId: number, memberId: number, role: string): Observable<void> {
    return this.http
      .patch<void>(`${API_ENDPOINTS.groups}/${roomId}/members/${memberId}/role`, null, {
        headers: this.userHeaders(),
        params: { role },
      })
      .pipe(catchError(() => of(void 0)));
  }

  getGroupInvites(roomId: number): Observable<InviteResponse[]> {
    return this.http
      .get<InviteResponse[]>(`${API_ENDPOINTS.groups}/${roomId}/invites`)
      .pipe(catchError(() => of([])));
  }

  createInvite(roomId: number, payload: CreateInviteRequest): Observable<InviteResponse> {
    return this.http
      .post<InviteResponse>(`${API_ENDPOINTS.groups}/${roomId}/invites`, payload, {
        headers: this.userHeaders(),
      })
      .pipe(
        catchError(() =>
          of({
            id: Date.now(),
            roomId,
            invitedUserId: payload.invitedUserId ?? 0,
            invitedEmail: payload.invitedEmail ?? '',
            invitedBy: 0,
            token: '',
            status: 'PENDING',
            expiresAt: '',
            createdAt: new Date().toISOString(),
            respondedAt: '',
          } as InviteResponse),
        ),
      );
  }

  respondInvite(inviteId: number, response: string): Observable<InviteResponse> {
    return this.http
      .post<InviteResponse>(
        `${API_ENDPOINTS.groups}/invites/${inviteId}/respond`,
        {},
        { headers: this.userHeaders(), params: { response } },
      )
      .pipe(
        catchError(() =>
          of({
            id: inviteId,
            roomId: 0,
            invitedUserId: 0,
            invitedEmail: '',
            invitedBy: 0,
            token: '',
            status: response,
            expiresAt: '',
            createdAt: '',
            respondedAt: new Date().toISOString(),
          } as InviteResponse),
        ),
      );
  }

  getGroupReports(roomId: number): Observable<ReportResponse[]> {
    return this.http
      .get<ReportResponse[]>(`${API_ENDPOINTS.groups}/${roomId}/reports`)
      .pipe(catchError(() => of([])));
  }

  createReport(roomId: number, payload: CreateReportRequest): Observable<ReportResponse> {
    return this.http
      .post<ReportResponse>(`${API_ENDPOINTS.groups}/${roomId}/reports`, payload, {
        headers: this.userHeaders(),
      })
      .pipe(
        catchError(() =>
          of({
            id: Date.now(),
            roomId,
            reportedBy: 0,
            reportedUserId: payload.reportedUserId,
            reason: payload.reason,
            description: payload.description ?? '',
            status: 'PENDING',
            resolvedBy: 0,
            resolutionNote: '',
            createdAt: new Date().toISOString(),
            resolvedAt: '',
          } as ReportResponse),
        ),
      );
  }

  resolveReport(reportId: number, resolution: string, note: string): Observable<ReportResponse> {
    return this.http
      .put<ReportResponse>(`${API_ENDPOINTS.groups}/reports/${reportId}/resolve`, null, {
        headers: this.userHeaders(),
        params: { resolution, note },
      })
      .pipe(
        catchError(() =>
          of({
            id: reportId,
            roomId: 0,
            reportedBy: 0,
            reportedUserId: 0,
            reason: '',
            description: '',
            status: 'RESOLVED',
            resolvedBy: 0,
            resolutionNote: note,
            createdAt: '',
            resolvedAt: new Date().toISOString(),
          } as ReportResponse),
        ),
      );
  }

  getGroupBans(roomId: number): Observable<BanResponse[]> {
    return this.http
      .get<BanResponse[]>(`${API_ENDPOINTS.groups}/${roomId}/bans`)
      .pipe(catchError(() => of([])));
  }

  banUser(
    roomId: number,
    userId: number,
    reason: string,
    permanent: boolean,
  ): Observable<BanResponse> {
    return this.http
      .post<BanResponse>(`${API_ENDPOINTS.groups}/${roomId}/bans`, null, {
        headers: this.userHeaders(),
        params: { userId: userId.toString(), reason, permanent: permanent.toString() },
      })
      .pipe(
        catchError(() =>
          of({
            id: Date.now(),
            roomId,
            userId,
            bannedBy: 0,
            reason,
            expiresAt: '',
            isPermanent: permanent,
            createdAt: new Date().toISOString(),
          }),
        ),
      );
  }

  unbanUser(roomId: number, userId: number): Observable<void> {
    return this.http
      .delete<void>(`${API_ENDPOINTS.groups}/${roomId}/bans/${userId}`, {
        headers: this.userHeaders(),
      })
      .pipe(catchError(() => of(void 0)));
  }
}
