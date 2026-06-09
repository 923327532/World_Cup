export interface PredictionDTO {
  id: number;
  userId: number;
  matchId: number;
  roomId: number;
  homeTeam: string;
  awayTeam: string;
  predictionTypeId: number;
  predictionType: string;
  predictionValue: string;
  points: number;
  createdAt: string;
  updatedAt: string;
  isLocked: boolean;
}

export interface PredictionTypeDTO {
  id: number;
  code: string;
  name: string;
  points: number;
}

export interface CreatePredictionRequest {
  matchId: number;
  roomId: number;
  predictionTypeId: number;
  predictionValue: string;
}

export interface UpdatePredictionRequest {
  predictionValue: string;
}
