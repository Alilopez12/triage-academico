import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UsuarioResponse, UsuarioCreateRequest, RolUsuario } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  listar(rol?: RolUsuario, activo?: boolean): Observable<UsuarioResponse[]> {
    let params = new HttpParams();
    if (rol) params = params.set('rol', rol);
    if (activo != null) params = params.set('activo', String(activo));
    return this.http.get<UsuarioResponse[]>(`${this.apiUrl}/usuarios`, { params });
  }

  crear(req: UsuarioCreateRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(`${this.apiUrl}/usuarios`, req);
  }
}
