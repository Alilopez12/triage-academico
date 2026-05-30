import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SolicitudService } from '../../../core/services/solicitud.service';
import { TipoSolicitud, CanalOrigen } from '../../../core/models/solicitud.model';

@Component({
  selector: 'app-solicitud-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './solicitud-form.component.html',
  styleUrl: './solicitud-form.component.scss'
})
export class SolicitudFormComponent {
  private readonly solicitudService = inject(SolicitudService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly tipoOptions: TipoSolicitud[] = [
    'HOMOLOGACION',
    'CANCELACION_ASIGNATURAS',
    'REGISTRO_ASIGNATURAS',
    'CONSULTA_ACADEMICA',
    'SOLICITUD_CUPOS'
  ];

  readonly canalOptions: CanalOrigen[] = ['CORREO', 'PRESENCIAL', 'TELEFONICO', 'CSU', 'SAC'];

  form = this.fb.group({
    tipo: [null as TipoSolicitud | null, Validators.required],
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
    canalOrigen: [null as CanalOrigen | null, Validators.required]
  });

  loading = false;
  errorMessage = '';

  get descripcionLength(): number {
    return this.form.get('descripcion')?.value?.length ?? 0;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.errorMessage = '';

    const { tipo, descripcion, canalOrigen } = this.form.value;
    this.solicitudService.crear({
      tipo: tipo!,
      descripcion: descripcion!,
      canalOrigen: canalOrigen!
    }).subscribe({
      next: (sol) => {
        this.loading = false;
        this.router.navigate(['/solicitudes', sol.id]);
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Error al crear la solicitud. Intente nuevamente.';
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/solicitudes']);
  }
}
