export interface RankingEntry {
  rankingPosition: number;
  userId: number;
  points: number;
}

export interface Badge {
  id: number;
  name: string;
  description: string;
  icon: string;
  unlocked: boolean;
  progress?: number;
}
