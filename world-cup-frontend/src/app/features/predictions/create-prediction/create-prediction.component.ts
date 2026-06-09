import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { startWith, switchMap } from 'rxjs';
import { GroupApiService } from '../../../services/api/group-api.service';
import { PredictionApiService } from '../../../services/api/prediction-api.service';
import { MatchApiService } from '../../../services/api/match-api.service';
import { NotificationService } from '../../../core/services/notification.service';
import { PredictionTypeDTO } from '../../../models/prediction.model';

@Component({
  selector: 'app-create-prediction',
  templateUrl: './create-prediction.component.html',
  styleUrls: ['./create-prediction.component.scss'],
  standalone: false,
})
export class CreatePredictionComponent {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly matchApiService = inject(MatchApiService);
  private readonly groupApiService = inject(GroupApiService);
  private readonly predictionApiService = inject(PredictionApiService);
  private readonly notificationService = inject(NotificationService);
  readonly selectedDate = new Date().toISOString().slice(0, 10);
  readonly matches$ = this.matchApiService.getMatchesByDate(this.selectedDate);
  readonly groups$ = this.groupApiService.getGroups();
  readonly predictionTypes$ = this.predictionApiService.getPredictionTypes();
  readonly initialRoomId = Number(this.route.snapshot.queryParamMap.get('roomId') || 1);
  readonly form = this.fb.group({
    roomId: [this.initialRoomId, Validators.required],
    matchId: [1, Validators.required],
    predictionTypeId: [1, Validators.required],
    predictionValue: ['', Validators.required],
  });
  readonly selectedMatch$ = this.form.controls.matchId.valueChanges.pipe(
    startWith(this.form.controls.matchId.value ?? 1),
    switchMap((matchId) => this.matchApiService.getMatch(Number(matchId || 1))),
  );

  submit(): void {
    if (this.form.invalid) return;
    this.predictionApiService.createPrediction(this.form.getRawValue() as any).subscribe(() => {
      this.notificationService.success('Prediccion guardada');
    });
  }
}
