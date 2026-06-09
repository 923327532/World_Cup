export interface RoomResponse {
  id: number;
  name: string;
  description: string;
  inviteCode: string;
  createdBy: number;
  maxMembers: number;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface MemberResponse {
  id: number;
  roomId: number;
  userId: number;
  role: string;
  joinedAt: string;
}

export interface InviteResponse {
  id: number;
  roomId: number;
  invitedUserId: number;
  invitedEmail: string;
  invitedBy: number;
  token: string;
  status: string;
  expiresAt: string;
  createdAt: string;
  respondedAt: string;
}

export interface ReportResponse {
  id: number;
  roomId: number;
  reportedBy: number;
  reportedUserId: number;
  reason: string;
  description: string;
  status: string;
  resolvedBy: number;
  resolutionNote: string;
  createdAt: string;
  resolvedAt: string;
}

export interface BanResponse {
  id: number;
  roomId: number;
  userId: number;
  bannedBy: number;
  reason: string;
  expiresAt: string;
  isPermanent: boolean;
  createdAt: string;
}

export interface CreateGroupRequest {
  name: string;
  description?: string;
  maxMembers?: number;
}

export interface CreateInviteRequest {
  invitedUserId?: number;
  invitedEmail?: string;
}

export interface CreateReportRequest {
  reportedUserId: number;
  reason: string;
  description?: string;
}
