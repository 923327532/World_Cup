import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ErrorHandlerService {
  getMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) return 'No se pudo conectar con el servidor.';
      if (error.status === 401) return 'Tu sesion expiro. Inicia sesion nuevamente.';
      if (error.status === 429) return 'Hay muchas solicitudes. Intenta otra vez en unos segundos.';
      return error.error?.message || `Error ${error.status}`;
    }
    return 'Ocurrio un error inesperado.';
  }
}
