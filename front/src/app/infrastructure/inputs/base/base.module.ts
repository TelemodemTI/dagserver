import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { AuthenticatedComponent } from './authenticated/authenticated.component';
import { HomeComponent } from './home/home.component';
import { ErrorMondalComponent } from './error-mondal/error-mondal.component';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { DagPropsComponent } from './dag-props/dag-props.component';
import { DagOpsComponent } from './dag-ops/dag-ops.component';
import { DagCanvasComponent } from './dag-canvas/dag-canvas.component';
import { ValueModalComponent } from './value-modal/value-modal.component';
import { ResultStepModalComponent } from './result-step-modal/result-step-modal.component';
import { DagParamsComponent } from './dag-params/dag-params.component';
import { UploadModalComponent } from './upload-modal/upload-modal.component';



@NgModule({
  declarations: [
    LoginComponent,
    AuthenticatedComponent,
    HomeComponent,
    ErrorMondalComponent,
    DagPropsComponent,
    DagOpsComponent,
    DagCanvasComponent,
    ValueModalComponent,
    ResultStepModalComponent,
    DagParamsComponent,
    UploadModalComponent,
  ],
  imports: [
    CommonModule,
    AppRoutingModule,
  ],
  exports: [
    LoginComponent,
    AuthenticatedComponent,
    HomeComponent,
    ErrorMondalComponent,
    DagPropsComponent,
    DagOpsComponent,
    DagCanvasComponent,
    DagCanvasComponent,
    DagParamsComponent,
    ValueModalComponent,
    UploadModalComponent,
    ResultStepModalComponent,
  ]
})
export class BaseModule { }
