import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseModule } from '../base/base.module';
import { ExceptionsListComponent } from './exceptions-list/exceptions-list.component';



@NgModule({
  declarations: [
    ExceptionsListComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ]
})
export class ExceptionsModule { }
