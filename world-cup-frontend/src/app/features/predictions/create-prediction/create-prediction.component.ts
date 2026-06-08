import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { PredictionApiService } from '../../../services/api/prediction-api.service';
import { MatchApiService } from '../../../services/api/match-api.service';
import { NotificationService } from '../../../core/services/notification.service';
import { PredictionType } from '../../../models/prediction.model';

@Component({
  selector: 'app-create-prediction',
  templateUrl: './create-prediction.component.html',
  styleUrls: ['./create-prediction.component.scss'],
  standalone: false
})
export class CreatePredictionComponent {
  private readonly fb = inject(FormBuilder);
  private readonly matchApiService = inject(MatchApiService);
  private readonly predictionApiService = inject(PredictionApiService);
  private readonly notificationService = inject(NotificationService);
  readonly selectedDate = new Date().toISOString().slice(0, 10);
  readonly matches$ = this.matchApiService.getMatchesByDate(this.selectedDate);
  readonly predictionTypes$ = this.predictionApiService.getPredictionTypes();
  readonly form = this.fb.group({
    matchId: [1, Validators.required],
    predictionTypeId: [1, Validators.required],
    predictionValue: ['', Validators.required]
  });

  submit(): void {
    if (this.form.invalid) return;
    this.predictionApiService.createPrediction(this.form.getRawValue() as any).subscribe(() => {
      this.notificationService.success('Prediccion guardada');
    });
  }
}
