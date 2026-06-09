export type MatchStatus = 'SCHEDULED' | 'LIVE' | 'FINISHED';

export interface Team {
  id: number;
  name: string;
  code: string;
  flagUrl?: string;
}

export interface MatchEvent {
  minute: number;
  type: 'Goal' | 'Card' | 'Substitution';
  player: string;
  team: string;
}

export interface Match {
  id: number;
  stadiumId?: number;
  stadiumName?: string;
  stadiumCity?: string;
  stadiumCountry?: string;
  stadiumCapacity?: number;
  venue?: string;
  stage?: string;
  groupName?: string;
  homeTeam: string;
  awayTeam: string;
  homeTeamId?: number;
  awayTeamId?: number;
  homeScore?: number;
  awayScore?: number;
  kickoffTime: string;
  status: MatchStatus | string;
}
