import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuickHomeComponent } from './quick-home/quick-home.component';



@NgModule({
  declarations: [
    QuickHomeComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    QuickHomeComponent
  ]
})
export class QuickModule { }
