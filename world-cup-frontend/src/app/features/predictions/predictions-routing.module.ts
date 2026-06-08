import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreatePredictionComponent } from './create-prediction/create-prediction.component';
import { PredictionHistoryComponent } from './prediction-history/prediction-history.component';

const routes: Routes = [
  { path: '', component: CreatePredictionComponent },
  { path: 'history', component: PredictionHistoryComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PredictionsRoutingModule {}
