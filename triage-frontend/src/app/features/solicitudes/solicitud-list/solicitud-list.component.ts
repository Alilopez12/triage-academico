import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { SolicitudService } from '../../../core/services/solicitud.service';
import {
  SolicitudResponse,
  EstadoSolicitud,
  TipoSolicitud,
  Prioridad
} from '../../../core/models/solicitud.model';

@Component({
  selector: 'app-solicitud-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatChipsModule
  ],
  templateUrl: './solicitud-list.component.html',
  styleUrl: './solicitud-list.component.scss'
})
export class SolicitudListComponent implements OnInit {
  private readonly solicitudService = inject(SolicitudService);
  private readonly fb = inject(FormBuilder);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  displayedColumns: string[] = ['id', 'tipo', 'estado', 'prioridad', 'nombreSolicitante', 'fechaRegistro', 'acciones'];
  dataSource: SolicitudResponse[] = [];
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;
  loading = false;

  readonly estadoOptions: EstadoSolicitud[] = ['REGISTRADA', 'CLASIFICADA', 'EN_ATENCION', 'ATENDIDA', 'CERRADA'];
  readonly tipoOptions: TipoSolicitud[] = ['HOMOLOGACION', 'CANCELACION_ASIGNATURAS', 'REGISTRO_ASIGNATURAS', 'CONSULTA_ACADEMICA', 'SOLICITUD_CUPOS'];
  readonly prioridadOptions: Prioridad[] = ['BAJA', 'MEDIA', 'ALTA', 'CRITICA'];

  filterForm = this.fb.group({
    estado: [null as EstadoSolicitud | null],
    tipo: [null as TipoSolicitud | null],
    prioridad: [null as Prioridad | null]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    const { estado, tipo, prioridad } = this.filterForm.value;
    this.solicitudService.listar({
      estado: estado ?? undefined,
      tipo: tipo ?? undefined,
      prioridad: prioridad ?? undefined,
      page: this.pageIndex,
      size: this.pageSize,
      sortBy: 'fechaRegistro',
      direction: 'desc'
    }).subscribe({
      next: (page) => {
        this.dataSource = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  buscar(): void {
    this.pageIndex = 0;
    this.load();
  }

  limpiarFiltros(): void {
    this.filterForm.reset();
    this.pageIndex = 0;
    this.load();
  }

  getPrioridadColor(prioridad?: Prioridad): string {
    switch (prioridad) {
      case 'CRITICA': return 'warn';
      case 'ALTA': return 'accent';
      default: return 'primary';
    }
  }
}
