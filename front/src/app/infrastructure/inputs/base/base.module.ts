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



@NgModule({
  declarations: [
    LoginComponent,
    AuthenticatedComponent,
    HomeComponent,
    ErrorMondalComponent,
    DagPropsComponent,
    DagOpsComponent,
    DagCanvasComponent
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
    DagCanvasComponent
  ]
})
export class BaseModule { }
