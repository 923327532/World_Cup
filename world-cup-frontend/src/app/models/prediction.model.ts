export interface Prediction {
  id?: number;
  userId?: number;
  matchId: number;
  predictionTypeId: number;
  predictionType?: string;
  predictionValue: string;
  points?: number;
  isLocked?: boolean;
  createdAt?: string;
  updatedAt?: string;
  matchLabel?: string;
}

export interface PredictionType {
  id: number;
  code: string;
  name: string;
  points: number;
}
