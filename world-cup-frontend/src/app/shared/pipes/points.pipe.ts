import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'points', standalone: false })
export class PointsPipe implements PipeTransform {
  transform(value = 0): string {
    return `${value} pts`;
  }
}
