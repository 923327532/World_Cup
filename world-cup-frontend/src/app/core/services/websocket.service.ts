import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject, interval, map, shareReplay } from 'rxjs';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs/lib/stomp.min';
import { environment } from '../../../environments/environment';

export interface ScoreUpdate {
  matchId: number;
  homeScore: number;
  awayScore: number;
  minute: number;
}

export interface ChatMessage {
  id?: number;
  userId?: number;
  matchId: number;
  content: string;
  createdAt?: string;
}

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  private stompClient?: any;
  private connected = false;
  private connecting = false;
  private readonly connectionSubject = new BehaviorSubject(false);
  readonly connectionState$ = this.connectionSubject.asObservable();
  private readonly matchSubscriptions = new Map<number, any>();
  private readonly matchSubjects = new Map<number, Subject<ChatMessage>>();
  private readonly commentsSubject = new Subject<string>();
  private readonly scores$ = interval(15000).pipe(
    map((tick) => ({
      matchId: 1,
      homeScore: 1 + (tick % 2),
      awayScore: tick % 2,
      minute: 24 + tick
    })),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  subscribeToLiveScores(): Observable<ScoreUpdate> {
    return this.scores$;
  }

  connect(): void {
    if (this.connected || this.connecting) return;

    this.connecting = true;
    const socket = new SockJS(environment.wsUrl);
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => undefined;
    this.stompClient.connect({}, () => {
      this.connected = true;
      this.connecting = false;
      this.connectionSubject.next(true);
      Array.from(this.matchSubjects.keys()).forEach((matchId) => this.ensureMatchSubscription(matchId));
    }, () => {
      this.connected = false;
      this.connecting = false;
      this.connectionSubject.next(false);
    });
  }

  disconnect(): void {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect(() => {
        this.connected = false;
        this.connectionSubject.next(false);
      });
    }
  }

  subscribeToMatchChat(matchId: number): Observable<ChatMessage> {
    if (!this.matchSubjects.has(matchId)) {
      this.matchSubjects.set(matchId, new Subject<ChatMessage>());
    }

    this.connect();
    this.ensureMatchSubscription(matchId);
    return this.matchSubjects.get(matchId)!.asObservable();
  }

  sendMatchComment(matchId: number, comment: ChatMessage): void {
    if (!this.connected || !this.stompClient) {
      this.matchSubjects.get(matchId)?.next(comment);
      return;
    }

    this.stompClient.send(`/app/chat/${matchId}`, {}, JSON.stringify(comment));
  }

  subscribeToComments(): Observable<string> {
    return this.commentsSubject.asObservable();
  }

  publishComment(comment: string): void {
    this.commentsSubject.next(comment);
  }

  private ensureMatchSubscription(matchId: number): void {
    if (!this.connected || !this.stompClient || this.matchSubscriptions.has(matchId)) return;

    const subscription = this.stompClient.subscribe(`/topic/chat/${matchId}`, (message: { body: string }) => {
      this.matchSubjects.get(matchId)?.next(JSON.parse(message.body) as ChatMessage);
    });
    this.matchSubscriptions.set(matchId, subscription);
  }
}
