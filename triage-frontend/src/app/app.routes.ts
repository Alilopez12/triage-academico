import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: '',
    redirectTo: 'solicitudes',
    pathMatch: 'full'
  },
  {
    path: 'solicitudes',
    loadComponent: () =>
      import('./shared/layout/layout.component').then((m) => m.LayoutComponent),
    children: [
      {
        path: '',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/solicitudes/solicitud-list/solicitud-list.component').then(
            (m) => m.SolicitudListComponent
          )
      },
      {
        path: 'nueva',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/solicitudes/solicitud-form/solicitud-form.component').then(
            (m) => m.SolicitudFormComponent
          )
      },
      {
        path: ':id',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/solicitudes/solicitud-detail/solicitud-detail.component').then(
            (m) => m.SolicitudDetailComponent
          )
      }
    ]
  },
  {
    path: 'usuarios',
    loadComponent: () =>
      import('./shared/layout/layout.component').then((m) => m.LayoutComponent),
    children: [
      {
        path: '',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/usuarios/usuario-list/usuario-list.component').then(
            (m) => m.UsuarioListComponent
          )
      },
      {
        path: 'nuevo',
        canActivate: [authGuard],
        loadComponent: () =>
          import('./features/usuarios/usuario-form/usuario-form.component').then(
            (m) => m.UsuarioFormComponent
          )
      }
    ]
  }
];
