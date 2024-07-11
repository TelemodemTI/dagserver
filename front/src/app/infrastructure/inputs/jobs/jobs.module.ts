import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JardetailComponent } from './jardetail/jardetail.component';
import { JobsComponent } from './jobs/jobs.component';
import { NewjComponent } from './newj/newj.component';
import { ExistingjComponent } from './existingj/existingj.component';
import { BaseModule } from '../base/base.module';
import { CompiledTabComponent } from './compiled-tab/compiled-tab.component';
import { UncompiledTabComponent } from './uncompiled-tab/uncompiled-tab.component';
import { ParamExistingjComponent } from './param-existingj/param-existingj.component';
import { JardetailpComponent } from './jardetailp/jardetailp.component';
import { DependenciesComponent } from './dependencies/dependencies.component';
import { ParamEditorModule } from '../param-editor/param-editor.module';



@NgModule({
  declarations: [
    JardetailComponent,
    JobsComponent,
    NewjComponent,
    ExistingjComponent,
    CompiledTabComponent,
    UncompiledTabComponent,
    ParamExistingjComponent,
    JardetailpComponent,
    DependenciesComponent
  ],
  imports: [
    CommonModule,
    BaseModule,
    ParamEditorModule
  ],
  exports: [
    JardetailComponent,
    JobsComponent,
    NewjComponent,
    ExistingjComponent,
    DependenciesComponent
  ]
})
export class JobsModule { }
