import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CredentialsComponent } from './credentials/credentials.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [
    CredentialsComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ]
})
export class CredentialsModule { }
