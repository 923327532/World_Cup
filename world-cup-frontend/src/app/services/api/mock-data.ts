import { Badge, RankingEntry } from '../../models/ranking.model';
import { Match } from '../../models/match.model';
import { Prediction } from '../../models/prediction.model';

export const MOCK_MATCHES: Match[] = [
  {
    id: 1,
    homeTeam: 'Mexico',
    awayTeam: 'Canada',
    homeScore: 1,
    awayScore: 0,
    kickoffTime: new Date().toISOString(),
    status: 'LIVE',
  },
  {
    id: 2,
    homeTeam: 'United States',
    awayTeam: 'Peru',
    kickoffTime: new Date(Date.now() + 86_400_000).toISOString(),
    status: 'SCHEDULED',
  },
  {
    id: 3,
    homeTeam: 'Argentina',
    awayTeam: 'France',
    homeScore: 2,
    awayScore: 2,
    kickoffTime: new Date(Date.now() - 86_400_000).toISOString(),
    status: 'FINISHED',
  }
];

export const MOCK_RANKINGS: RankingEntry[] = [
  { rankingPosition: 1, userId: 1, points: 148 },
  { rankingPosition: 2, userId: 2, points: 141 },
  { rankingPosition: 3, userId: 3, points: 132 },
  { rankingPosition: 4, userId: 4, points: 125 }
];

export const MOCK_BADGES: Badge[] = [
  { id: 1, name: 'Primer Gol', description: 'Acertaste tu primer marcador exacto.', icon: 'emoji_events', unlocked: true, progress: 100 },
  { id: 2, name: 'Racha Mundialista', description: 'Predice 5 partidos consecutivos.', icon: 'local_fire_department', unlocked: true, progress: 100 },
  { id: 3, name: 'Analista Pro', description: 'Llega a 100 puntos acumulados.', icon: 'analytics', unlocked: false, progress: 72 }
];

export const MOCK_PREDICTIONS: Prediction[] = [
  { id: 11, userId: 7, matchId: 1, predictionTypeId: 1, predictionType: 'Marcador exacto', predictionValue: '2-1', points: 8, isLocked: true, createdAt: new Date(Date.now() - 172_800_000).toISOString(), matchLabel: 'Mexico vs Canada' },
  { id: 12, userId: 7, matchId: 2, predictionTypeId: 1, predictionType: 'Marcador exacto', predictionValue: '1-1', points: 0, isLocked: false, createdAt: new Date(Date.now() - 86_400_000).toISOString(), matchLabel: 'United States vs Peru' }
];

export interface SocialComment {
  id: number;
  matchId: number;
  user: string;
  content: string;
  createdAt: string;
  reactions: Record<string, number>;
}

export const MOCK_COMMENTS: SocialComment[] = [
  { id: 1, matchId: 1, user: 'Ana Torres', content: 'Mexico esta presionando alto, se viene otro gol.', createdAt: new Date(Date.now() - 420_000).toISOString(), reactions: { '👍': 8, '🔥': 4 } },
  { id: 2, matchId: 1, user: 'Diego Ramos', content: 'Canada necesita cambiar el mediocampo ya.', createdAt: new Date(Date.now() - 180_000).toISOString(), reactions: { '👏': 3 } }
];
