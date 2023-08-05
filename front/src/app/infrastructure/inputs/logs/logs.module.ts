import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogsComponent } from './logs/logs.component';
import { LogdetailComponent } from './logdetail/logdetail.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [
    LogsComponent,
    LogdetailComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ],
  exports: [
    LogsComponent,
    LogdetailComponent
  ]
})
export class LogsModule { }
