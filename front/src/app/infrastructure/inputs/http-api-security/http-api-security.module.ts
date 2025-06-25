import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiKeyComponent } from './api-key/api-key.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [ApiKeyComponent],
  imports: [
    CommonModule, BaseModule
  ],
  exports: [
    ApiKeyComponent
  ]
})
export class HttpApiSecurityModule { }
