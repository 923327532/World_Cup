export interface Campus {
  id: number;
  name: string;
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
