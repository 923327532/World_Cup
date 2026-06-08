import { Component, inject } from '@angular/core';
import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss'],
  standalone: false
})
export class LoadingComponent {
  private readonly loadingService = inject(LoadingService);
  readonly loading$ = this.loadingService.loading$;
}
