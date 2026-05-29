export type RolUsuario = 'ADMIN' | 'ESTUDIANTE' | 'RESPONSABLE';

export interface UsuarioResponse {
  id: number;
  nombre: string;
  email: string;
  rol: RolUsuario;
  activo: boolean;
}

export interface UsuarioCreateRequest {
  nombre: string;
  email: string;
  password: string;
  rol: RolUsuario;
  activo: boolean;
}
