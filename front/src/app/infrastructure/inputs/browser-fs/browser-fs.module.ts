import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerComponent } from './explorer/explorer.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [
    ExplorerComponent,
  ],
  imports: [
    CommonModule,
    BaseModule
  ],
  exports: [
    ExplorerComponent
  ]
})
export class BrowserFSModule { }
