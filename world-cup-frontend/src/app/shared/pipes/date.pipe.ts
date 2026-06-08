import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'appDate', standalone: false })
export class AppDatePipe implements PipeTransform {
  transform(value?: string): string {
    if (!value) return '';
    return new Intl.DateTimeFormat('es-PE', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(value));
  }
}
