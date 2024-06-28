import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseModule } from '../base/base.module';
import { BrowserComponent } from './browser/browser.component';



@NgModule({
  declarations: [
    BrowserComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ]
})
export class XcomBrowserModule { }
