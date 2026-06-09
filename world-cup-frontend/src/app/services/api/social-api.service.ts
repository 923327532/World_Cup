import { HttpClient, HttpContext, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError, map, of, tap } from 'rxjs';
import { API_ENDPOINTS } from '../../core/constants/api.constants';
import { SILENT_ERROR } from '../../core/interceptors/error.interceptor';
import { AuthService } from '../../core/services/auth.service';
import { MOCK_COMMENTS, SocialComment } from './mock-data';

export interface BackendComment {
  id: number;
  userId: number;
  matchId: number;
  content: string;
  createdAt: string;
}

export interface ReactionRequest {
  commentId: number;
  reaction: string;
}

@Injectable({ providedIn: 'root' })
export class SocialApiService {
  private readonly commentsSubject = new BehaviorSubject<SocialComment[]>(MOCK_COMMENTS);

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  private userHeaders(): HttpHeaders {
    const userId = this.authService.getCurrentUser()?.userId;
    return userId ? new HttpHeaders().set('X-User-Id', String(userId)) : new HttpHeaders();
  }

  getComments(matchId: number): Observable<SocialComment[]> {
    return this.http
      .get<
        BackendComment[]
      >(`${API_ENDPOINTS.social}/comments/match/${matchId}`, { context: this.silentContext() })
      .pipe(
        map((comments) => comments.map((comment) => this.toView(comment))),
        tap((comments) => this.commentsSubject.next(comments)),
        catchError(() =>
          this.commentsSubject
            .asObservable()
            .pipe(map((comments) => comments.filter((comment) => comment.matchId === matchId))),
        ),
      );
  }

  addComment(matchId: number, content: string, user = 'capitan.tecsup'): Observable<SocialComment> {
    const payload = { matchId, content };
    return this.http
      .post<BackendComment>(`${API_ENDPOINTS.social}/comments`, payload, {
        headers: this.userHeaders(),
        context: this.silentContext(),
      })
      .pipe(
        map((comment) => this.toView(comment, user)),
        tap((comment) => this.commentsSubject.next([comment, ...this.commentsSubject.value])),
        catchError(() => {
          const comment: SocialComment = {
            id: Date.now(),
            matchId,
            user,
            content,
            createdAt: new Date().toISOString(),
            reactions: {},
          };
          this.commentsSubject.next([comment, ...this.commentsSubject.value]);
          return of(comment);
        }),
      );
  }

  addReaction(commentId: number, reaction: string): Observable<void> {
    const payload: ReactionRequest = { commentId, reaction };
    return this.http
      .post<void>(`${API_ENDPOINTS.social}/reactions`, payload, {
        headers: this.userHeaders(),
        context: this.silentContext(),
      })
      .pipe(
        catchError(() => {
          const comments = this.commentsSubject.value.map((comment) => {
            if (comment.id !== commentId) return comment;
            return {
              ...comment,
              reactions: {
                ...comment.reactions,
                [reaction]: (comment.reactions[reaction] || 0) + 1,
              },
            };
          });
          this.commentsSubject.next(comments);
          return of(void 0);
        }),
      );
  }

  removeReaction(commentId: number): Observable<void> {
    return this.http
      .delete<void>(`${API_ENDPOINTS.social}/reactions/${commentId}`, {
        headers: this.userHeaders(),
        context: this.silentContext(),
      })
      .pipe(
        catchError(() => {
          this.commentsSubject.next(
            this.commentsSubject.value.map((comment) => ({ ...comment, reactions: {} })),
          );
          return of(void 0);
        }),
      );
  }

  private toView(comment: BackendComment, user = `User ${comment.userId}`): SocialComment {
    return {
      id: comment.id,
      matchId: comment.matchId,
      user,
      content: comment.content,
      createdAt: comment.createdAt,
      reactions: {},
    };
  }

  private silentContext(): HttpContext {
    return new HttpContext().set(SILENT_ERROR, true);
  }
}
