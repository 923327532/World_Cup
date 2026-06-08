import { environment } from '../../../environments/environment';

export const API_ENDPOINTS = {
  auth: `${environment.apiUrl}/auth`,
  organization: `${environment.apiUrl}/organization`,
  worldcup: `${environment.apiUrl}/worldcup`,
  matches: `${environment.apiUrl}/matches`,
  teams: `${environment.apiUrl}/teams`,
  tournaments: `${environment.apiUrl}/tournaments`,
  campuses: `${environment.apiUrl}/organization/campuses`,
  departments: `${environment.apiUrl}/organization/departments`,
  careers: `${environment.apiUrl}/organization/careers`,
  predictions: `${environment.apiUrl}/predictions`,
  predictionTypes: `${environment.apiUrl}/prediction-types`,
  leaderboard: `${environment.apiUrl}/leaderboard`,
  social: `${environment.apiUrl}/social`,
  badges: `${environment.apiUrl}/gamification/badges`,
  rewards: `${environment.apiUrl}/gamification/rewards`,
  notifications: `${environment.apiUrl}/notifications`,
  scoring: `${environment.apiUrl}/scoring`
};

export const CACHE_TTL = {
  liveMatches: 15_000,
  scheduledMatches: 3_600_000,
  standings: 300_000,
  rankings: 300_000
};
