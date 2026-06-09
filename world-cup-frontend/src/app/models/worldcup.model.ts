export interface TournamentDTO {
  id: number;
  name: string;
  year: number;
  startDate: string;
  endDate: string;
  status: string;
}

export interface TeamDTO {
  id: number;
  name: string;
  code: string;
  flagUrl?: string;
  groupName?: string;
}

export interface PlayerDTO {
  id: number;
  name: string;
  position: string;
  number: number;
  teamId: number;
  photoUrl?: string;
}

export interface MatchDTO {
  id: number;
  homeTeamId: number;
  awayTeamId: number;
  homeTeam: string;
  awayTeam: string;
  homeScore?: number;
  awayScore?: number;
  kickoffTime: string;
  status: 'SCHEDULED' | 'LIVE' | 'FINISHED' | string;
  venue?: string;
  stage?: string;
  groupName?: string;
}
