import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { PredictionsRoutingModule } from './predictions-routing.module';
import { CreatePredictionComponent } from './create-prediction/create-prediction.component';
import { PredictionHistoryComponent } from './prediction-history/prediction-history.component';

@NgModule({
  declarations: [CreatePredictionComponent, PredictionHistoryComponent],
  imports: [SharedModule, ReactiveFormsModule, PredictionsRoutingModule]
})
export class PredictionsModule {}
