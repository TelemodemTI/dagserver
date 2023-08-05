import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PropsComponent } from './props/props.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [
    PropsComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ],
  exports: [
    PropsComponent
  ]
})
export class PropsModule { }
