export type TipoSolicitud = 'HOMOLOGACION' | 'CANCELACION_ASIGNATURAS' | 'REGISTRO_ASIGNATURAS' | 'CONSULTA_ACADEMICA' | 'SOLICITUD_CUPOS';
export type CanalOrigen = 'CORREO' | 'PRESENCIAL' | 'TELEFONICO' | 'CSU' | 'SAC';
export type EstadoSolicitud = 'REGISTRADA' | 'CLASIFICADA' | 'EN_ATENCION' | 'ATENDIDA' | 'CERRADA';
export type Prioridad = 'BAJA' | 'MEDIA' | 'ALTA' | 'CRITICA';
export type ImpactoAcademico = 'BAJO' | 'MEDIO' | 'ALTO';

export interface HistorialItem {
  accion: string;
  observaciones: string;
  fechaHora: string;
  usuarioResponsableId: number;
}

export interface SolicitudResponse {
  id: number;
  version: number;
  tipo: TipoSolicitud;
  descripcion: string;
  canalOrigen: CanalOrigen;
  estado: EstadoSolicitud;
  prioridad?: Prioridad;
  impactoAcademico?: ImpactoAcademico;
  fechaLimite?: string;
  justificacionPrioridad?: string;
  fechaRegistro: string;
  solicitanteId: number;
  nombreSolicitante: string;
  responsableAsignadoId?: number;
  nombreResponsableAsignado?: string;
  observacionCierre?: string;
  historial: HistorialItem[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  sortBy: string;
  direction: string;
}

export interface SolicitudCreateRequest {
  tipo: TipoSolicitud;
  descripcion: string;
  canalOrigen: CanalOrigen;
}

export interface ClasificarRequest {
  tipo: TipoSolicitud;
  version: number;
}

export interface AsignarPrioridadRequest {
  impactoAcademico: ImpactoAcademico;
  fechaLimite: string;
  version: number;
}

export interface AsignarResponsableRequest {
  responsableId: number;
  version: number;
}

export interface CambiarEstadoRequest {
  nuevoEstado: EstadoSolicitud;
  observacion?: string;
  version: number;
}

export interface CerrarRequest {
  observacionCierre: string;
  version: number;
}
