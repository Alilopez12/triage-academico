export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  nombre: string;
  email: string;
  rol: 'ADMIN' | 'ESTUDIANTE' | 'RESPONSABLE';
}

export interface UsuarioAutenticado {
  userId: number;
  nombre: string;
  email: string;
  rol: 'ADMIN' | 'ESTUDIANTE' | 'RESPONSABLE';
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
  rol: 'ESTUDIANTE' | 'RESPONSABLE';
}
