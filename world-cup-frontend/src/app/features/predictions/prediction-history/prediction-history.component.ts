import { Component, inject } from '@angular/core';
import { PredictionApiService } from '../../../services/api/prediction-api.service';

@Component({
  selector: 'app-prediction-history',
  templateUrl: './prediction-history.component.html',
  styleUrls: ['./prediction-history.component.scss'],
  standalone: false
})
export class PredictionHistoryComponent {
  private readonly predictionApiService = inject(PredictionApiService);
  readonly predictions$ = this.predictionApiService.getMyPredictions();
}
