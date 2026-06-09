export interface RankingEntry {
  rankingPosition: number;
  userId: number;
  points: number;
}

export interface LeaderboardEntryDTO {
  rankingPosition: number;
  userId: number;
  email: string;
  firstName?: string;
  lastName?: string;
  points: number;
  campusName?: string;
  careerName?: string;
  departmentName?: string;
}

export interface Badge {
  id: number;
  name: string;
  description: string;
  icon: string;
  unlocked: boolean;
  progress?: number;
}

export interface BadgeDTO {
  id: number;
  name: string;
  description: string;
  icon: string;
  criteria: string;
  points: number;
}

export interface UserBadgeDTO {
  id: number;
  userId: number;
  badgeId: number;
  badge: BadgeDTO;
  earnedAt: string;
}

export interface RewardDTO {
  id: number;
  name: string;
  description: string;
  icon: string;
  requiredPoints: number;
  isActive: boolean;
}

export interface UserScoreDTO {
  userId: number;
  totalPoints: number;
  correctPredictions: number;
  totalPredictions: number;
}

export interface ScoreHistoryDTO {
  id: number;
  userId: number;
  points: number;
  reason: string;
  matchId?: number;
  createdAt: string;
}
