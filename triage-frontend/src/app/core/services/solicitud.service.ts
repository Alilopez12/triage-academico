import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  SolicitudResponse,
  PageResponse,
  SolicitudCreateRequest,
  ClasificarRequest,
  AsignarPrioridadRequest,
  AsignarResponsableRequest,
  CambiarEstadoRequest,
  CerrarRequest,
  HistorialItem,
  EstadoSolicitud,
  TipoSolicitud,
  Prioridad
} from '../models/solicitud.model';

export interface SolicitudFiltros {
  estado?: EstadoSolicitud;
  tipo?: TipoSolicitud;
  prioridad?: Prioridad;
  responsableId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: string;
}

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  listar(filtros: SolicitudFiltros): Observable<PageResponse<SolicitudResponse>> {
    let params = new HttpParams();
    if (filtros.estado) params = params.set('estado', filtros.estado);
    if (filtros.tipo) params = params.set('tipo', filtros.tipo);
    if (filtros.prioridad) params = params.set('prioridad', filtros.prioridad);
    if (filtros.responsableId != null) params = params.set('responsableId', String(filtros.responsableId));
    if (filtros.page != null) params = params.set('page', String(filtros.page));
    if (filtros.size != null) params = params.set('size', String(filtros.size));
    if (filtros.sortBy) params = params.set('sortBy', filtros.sortBy);
    if (filtros.direction) params = params.set('direction', filtros.direction);
    return this.http.get<PageResponse<SolicitudResponse>>(`${this.apiUrl}/solicitudes`, { params });
  }

  obtenerPorId(id: number): Observable<SolicitudResponse> {
    return this.http.get<SolicitudResponse>(`${this.apiUrl}/solicitudes/${id}`);
  }

  crear(req: SolicitudCreateRequest): Observable<SolicitudResponse> {
    return this.http.post<SolicitudResponse>(`${this.apiUrl}/solicitudes`, req);
  }

  clasificar(id: number, req: ClasificarRequest): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(`${this.apiUrl}/solicitudes/${id}/clasificar`, req);
  }

  asignarPrioridad(id: number, req: AsignarPrioridadRequest): Observable<SolicitudResponse> {
    return this.http.put<SolicitudResponse>(`${this.apiUrl}/solicitudes/${id}/prioridad`, req);
  }

  asignarResponsable(id: number, req: AsignarResponsableRequest): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(`${this.apiUrl}/solicitudes/${id}/asignar`, req);
  }

  cambiarEstado(id: number, req: CambiarEstadoRequest): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(`${this.apiUrl}/solicitudes/${id}/estado`, req);
  }

  cerrar(id: number, req: CerrarRequest): Observable<SolicitudResponse> {
    return this.http.put<SolicitudResponse>(`${this.apiUrl}/solicitudes/${id}/cerrar`, req);
  }

  obtenerHistorial(id: number): Observable<HistorialItem[]> {
    return this.http.get<HistorialItem[]>(`${this.apiUrl}/solicitudes/${id}/historial`);
  }
}
