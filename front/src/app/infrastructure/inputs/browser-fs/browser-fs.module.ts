import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExplorerComponent } from './explorer/explorer.component';
import { BaseModule } from '../base/base.module';
import { UploadFileComponent } from './upload-file/upload-file.component';



@NgModule({
  declarations: [
    UploadFileComponent,
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
