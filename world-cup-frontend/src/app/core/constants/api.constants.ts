import { environment } from '../../../environments/environment';

export const API_ENDPOINTS = {
  auth: `${environment.apiUrl}/auth`,
  organization: `${environment.apiUrl}/organization`,
  worldcup: `${environment.apiUrl}/worldcup`,
  matches: `${environment.apiUrl}/matches`,
  teams: `${environment.apiUrl}/teams`,
  tournaments: `${environment.apiUrl}/tournaments`,
  players: `${environment.apiUrl}/players`,
  campuses: `${environment.apiUrl}/organization/campuses`,
  departments: `${environment.apiUrl}/organization/departments`,
  careers: `${environment.apiUrl}/organization/careers`,
  teacherProfiles: `${environment.apiUrl}/organization/teacher-profiles`,
  studentProfiles: `${environment.apiUrl}/organization/student-profiles`,
  avatars: `${environment.apiUrl}/organization/avatars`,
  predictions: `${environment.apiUrl}/predictions`,
  predictionTypes: `${environment.apiUrl}/prediction-types`,
  leaderboard: `${environment.apiUrl}/leaderboard`,
  social: `${environment.apiUrl}/social`,
  badges: `${environment.apiUrl}/gamification/badges`,
  rewards: `${environment.apiUrl}/gamification/rewards`,
  notifications: `${environment.apiUrl}/notifications`,
  scoring: `${environment.apiUrl}/scoring`,
  admin: `${environment.apiUrl}/admin`,
  groups: `${environment.apiUrl}/groups`,
};

export const CACHE_TTL = {
  liveMatches: 15_000,
  scheduledMatches: 3_600_000,
  standings: 300_000,
  rankings: 300_000,
};
