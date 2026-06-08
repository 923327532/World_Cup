import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CacheService {
  private cache = new Map<string, { data: unknown; timestamp: number }>();

  get<T>(key: string, ttl: number): T | null {
    const cached = this.cache.get(key);
    if (!cached || Date.now() - cached.timestamp > ttl) {
      return null;
    }
    return cached.data as T;
  }

  set<T>(key: string, data: T): void {
    this.cache.set(key, { data, timestamp: Date.now() });
  }

  clear(): void {
    this.cache.clear();
  }
}
