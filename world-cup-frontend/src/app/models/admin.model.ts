export interface ManualMatchResponse {
  id: number;
  homeTeam: string;
  awayTeam: string;
  startTime: string;
  status: string;
  homeScore?: number;
  awayScore?: number;
  winner?: string;
  sourceType: string;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
  venue?: string;
  stage?: string;
  groupName?: string;
}

export interface ManualMatchRequest {
  homeTeam: string;
  awayTeam: string;
  startTime: string;
  venue?: string;
  stage?: string;
  groupName?: string;
}

export interface MatchResultRequest {
  homeScore: number;
  awayScore: number;
  winner?: string;
}

export interface AdminAuditLogResponse {
  id: number;
  adminId: number;
  adminEmail: string;
  action: string;
  entityType: string;
  entityId: number;
  details: string;
  createdAt: string;
}
