import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UsuarioService } from '../../../core/services/usuario.service';
import { RolUsuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './usuario-form.component.html',
  styleUrl: './usuario-form.component.scss'
})
export class UsuarioFormComponent {
  private readonly usuarioService = inject(UsuarioService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly rolOptions: RolUsuario[] = ['ADMIN', 'ESTUDIANTE', 'RESPONSABLE'];

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    rol: [null as RolUsuario | null, Validators.required],
    activo: [true]
  });

  loading = false;
  errorMessage = '';
  hidePassword = true;

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.errorMessage = '';

    const { nombre, email, password, rol, activo } = this.form.value;
    this.usuarioService.crear({
      nombre: nombre!,
      email: email!,
      password: password!,
      rol: rol!,
      activo: activo!
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/usuarios']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.errorMessage = 'Ya existe un usuario con ese email.';
        } else {
          this.errorMessage = 'Error al crear el usuario. Intente nuevamente.';
        }
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/usuarios']);
  }
}
