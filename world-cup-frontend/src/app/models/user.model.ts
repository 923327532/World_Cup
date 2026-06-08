export interface User {
  userId: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role: 'STUDENT' | 'TEACHER' | 'ADMIN';
  studentCode?: string;
  avatarUrl?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  userId: number;
  email: string;
  role: string;
}
