import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KeystoreContentComponent } from './keystore-content/keystore-content.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [
    KeystoreContentComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ]
})
export class KeystoreModule { }
