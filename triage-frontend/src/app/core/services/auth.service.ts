import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, RegisterRequest, UsuarioAutenticado } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;

  login(req: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, req).pipe(
      tap((res) => {
        localStorage.setItem('auth_token', res.token);
        const usuario: UsuarioAutenticado = {
          userId: res.userId,
          nombre: res.nombre,
          email: res.email,
          rol: res.rol
        };
        localStorage.setItem('usuario_actual', JSON.stringify(usuario));
      })
    );
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('usuario_actual');
    this.router.navigate(['/login']);
  }

  getUsuarioActual(): UsuarioAutenticado | null {
    const raw = localStorage.getItem('usuario_actual');
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UsuarioAutenticado;
    } catch {
      return null;
    }
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('auth_token');
  }

  register(req: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/register`, req).pipe(
      tap((res) => {
        localStorage.setItem('auth_token', res.token);
        const usuario: UsuarioAutenticado = {
          userId: res.userId,
          nombre: res.nombre,
          email: res.email,
          rol: res.rol
        };
        localStorage.setItem('usuario_actual', JSON.stringify(usuario));
      })
    );
  }

  isAdmin(): boolean {
    return this.getUsuarioActual()?.rol === 'ADMIN';
  }
}
