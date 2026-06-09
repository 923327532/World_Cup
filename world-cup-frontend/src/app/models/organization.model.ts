export interface Campus {
  id: number;
  name: string;
  address?: string;
}

export interface Department {
  id: number;
  name: string;
}

export interface Career {
  id: number;
  name: string;
  departmentId?: number;
}

export interface TeacherProfileDTO {
  id: number;
  userId: number;
  departmentId?: number;
  specialization?: string;
  bio?: string;
}

export interface StudentProfileDTO {
  id: number;
  userId: number;
  campusId?: number;
  careerId?: number;
  studentCode?: string;
  semester?: number;
}

export interface AvatarDTO {
  id: number;
  name: string;
  url: string;
  isDefault: boolean;
}
