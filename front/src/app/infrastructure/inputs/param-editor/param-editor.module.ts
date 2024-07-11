import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DefaultTypeParamComponent } from './default-type-param/default-type-param.component';
import { SourceTypeParamComponent } from './source-type-param/source-type-param.component';
import { RemoteTypeParamComponent } from './remote-type-param/remote-type-param.component';



@NgModule({
  declarations: [
    DefaultTypeParamComponent,
    SourceTypeParamComponent,
    RemoteTypeParamComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    DefaultTypeParamComponent,
    SourceTypeParamComponent,
    RemoteTypeParamComponent
  ]
})
export class ParamEditorModule { }
