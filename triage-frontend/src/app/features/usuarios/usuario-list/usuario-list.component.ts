import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { UsuarioService } from '../../../core/services/usuario.service';
import { UsuarioResponse, RolUsuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatTableModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatChipsModule
  ],
  templateUrl: './usuario-list.component.html',
  styleUrl: './usuario-list.component.scss'
})
export class UsuarioListComponent implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  private readonly fb = inject(FormBuilder);

  displayedColumns: string[] = ['id', 'nombre', 'email', 'rol', 'activo'];
  dataSource: UsuarioResponse[] = [];
  loading = false;

  readonly rolOptions: RolUsuario[] = ['ADMIN', 'ESTUDIANTE', 'RESPONSABLE'];

  filterForm = this.fb.group({
    rol: [null as RolUsuario | null]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    const { rol } = this.filterForm.value;
    this.usuarioService.listar(rol ?? undefined).subscribe({
      next: (users) => {
        this.dataSource = users;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  buscar(): void {
    this.load();
  }

  limpiarFiltros(): void {
    this.filterForm.reset();
    this.load();
  }
}
