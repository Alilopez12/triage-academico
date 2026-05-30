import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subject, takeUntil, timeout, TimeoutError } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { SolicitudService } from '../../../core/services/solicitud.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  SolicitudResponse,
  TipoSolicitud,
  EstadoSolicitud,
  ImpactoAcademico
} from '../../../core/models/solicitud.model';
import { UsuarioResponse } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-solicitud-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDividerModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    MatChipsModule
  ],
  templateUrl: './solicitud-detail.component.html',
  styleUrl: './solicitud-detail.component.scss'
})
export class SolicitudDetailComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly solicitudService = inject(SolicitudService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  solicitud: SolicitudResponse | null = null;
  loading = true;
  actionLoading = false;
  errorMessage = '';
  responsables: UsuarioResponse[] = [];

  readonly tipoOptions: TipoSolicitud[] = [
    'HOMOLOGACION',
    'CANCELACION_ASIGNATURAS',
    'REGISTRO_ASIGNATURAS',
    'CONSULTA_ACADEMICA',
    'SOLICITUD_CUPOS'
  ];

  readonly estadoOptions: EstadoSolicitud[] = ['REGISTRADA', 'CLASIFICADA', 'EN_ATENCION', 'ATENDIDA', 'CERRADA'];
  readonly impactoOptions: ImpactoAcademico[] = ['BAJO', 'MEDIO', 'ALTO'];

  clasificarForm = this.fb.group({
    tipo: [null as TipoSolicitud | null, Validators.required]
  });

  prioridadForm = this.fb.group({
    impactoAcademico: [null as ImpactoAcademico | null, Validators.required],
    fechaLimite: ['', Validators.required]
  });

  responsableForm = this.fb.group({
    responsableId: [null as number | null, Validators.required]
  });

  estadoForm = this.fb.group({
    nuevoEstado: [null as EstadoSolicitud | null, Validators.required],
    observacion: ['']
  });

  cerrarForm = this.fb.group({
    observacionCierre: ['', [Validators.required, Validators.minLength(10)]]
  });

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  get isResponsable(): boolean {
    return this.authService.getUsuarioActual()?.rol === 'RESPONSABLE';
  }

  get canClasificar(): boolean {
    return this.isAdmin && this.solicitud?.estado === 'REGISTRADA';
  }

  get canAsignarPrioridad(): boolean {
    return this.isAdmin && this.solicitud?.estado === 'CLASIFICADA';
  }

  get canAsignarResponsable(): boolean {
    return this.isAdmin && this.solicitud?.estado === 'CLASIFICADA';
  }

  get canCambiarEstado(): boolean {
    return (this.isAdmin || this.isResponsable) && this.solicitud?.estado !== 'CERRADA';
  }

  get canCerrar(): boolean {
    return this.isAdmin && this.solicitud?.estado === 'ATENDIDA';
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarSolicitud(id);
    this.cargarResponsables();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarSolicitud(id: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.solicitudService.obtenerPorId(id).pipe(
      timeout(15000),
      takeUntil(this.destroy$)
    ).subscribe({
      next: (sol) => {
        this.solicitud = { ...sol, historial: sol.historial ?? [] };
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err instanceof TimeoutError
          ? 'El servidor tardó demasiado en responder. Verifique que el backend esté activo.'
          : 'No se pudo cargar la solicitud.';
        this.cdr.detectChanges();
      }
    });
  }

  cargarResponsables(): void {
    this.usuarioService.listar('RESPONSABLE').pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (users) => {
        this.responsables = users;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  clasificar(): void {
    if (this.clasificarForm.invalid || !this.solicitud) return;
    this.actionLoading = true;
    const { tipo } = this.clasificarForm.value;
    this.solicitudService.clasificar(this.solicitud.id, {
      tipo: tipo!,
      version: this.solicitud.version
    }).subscribe({
      next: (sol) => {
        this.solicitud = sol;
        this.actionLoading = false;
        this.clasificarForm.reset();
      },
      error: () => {
        this.actionLoading = false;
        this.errorMessage = 'Error al clasificar la solicitud.';
      }
    });
  }

  asignarPrioridad(): void {
    if (this.prioridadForm.invalid || !this.solicitud) return;
    this.actionLoading = true;
    const { impactoAcademico, fechaLimite } = this.prioridadForm.value;
    this.solicitudService.asignarPrioridad(this.solicitud.id, {
      impactoAcademico: impactoAcademico!,
      fechaLimite: fechaLimite!,
      version: this.solicitud.version
    }).subscribe({
      next: (sol) => {
        this.solicitud = sol;
        this.actionLoading = false;
        this.prioridadForm.reset();
      },
      error: () => {
        this.actionLoading = false;
        this.errorMessage = 'Error al asignar prioridad.';
      }
    });
  }

  asignarResponsable(): void {
    if (this.responsableForm.invalid || !this.solicitud) return;
    this.actionLoading = true;
    const { responsableId } = this.responsableForm.value;
    this.solicitudService.asignarResponsable(this.solicitud.id, {
      responsableId: responsableId!,
      version: this.solicitud.version
    }).subscribe({
      next: (sol) => {
        this.solicitud = sol;
        this.actionLoading = false;
        this.responsableForm.reset();
      },
      error: () => {
        this.actionLoading = false;
        this.errorMessage = 'Error al asignar responsable.';
      }
    });
  }

  cambiarEstado(): void {
    if (this.estadoForm.invalid || !this.solicitud) return;
    this.actionLoading = true;
    const { nuevoEstado, observacion } = this.estadoForm.value;
    this.solicitudService.cambiarEstado(this.solicitud.id, {
      nuevoEstado: nuevoEstado!,
      observacion: observacion || undefined,
      version: this.solicitud.version
    }).subscribe({
      next: (sol) => {
        this.solicitud = sol;
        this.actionLoading = false;
        this.estadoForm.reset();
      },
      error: () => {
        this.actionLoading = false;
        this.errorMessage = 'Error al cambiar estado.';
      }
    });
  }

  cerrar(): void {
    if (this.cerrarForm.invalid || !this.solicitud) return;
    this.actionLoading = true;
    const { observacionCierre } = this.cerrarForm.value;
    this.solicitudService.cerrar(this.solicitud.id, {
      observacionCierre: observacionCierre!,
      version: this.solicitud.version
    }).subscribe({
      next: (sol) => {
        this.solicitud = sol;
        this.actionLoading = false;
        this.cerrarForm.reset();
      },
      error: () => {
        this.actionLoading = false;
        this.errorMessage = 'Error al cerrar la solicitud.';
      }
    });
  }

  volver(): void {
    this.router.navigate(['/solicitudes']);
  }
}
