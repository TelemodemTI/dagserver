import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './base/home/home.component';
import { QuickHomeComponent } from './quick/quick-home/quick-home.component';
import { DocBaseComponent } from './doc/doc-base/doc-base.component';
import { OpsBaseComponent } from './ops/ops-base/ops-base.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'quick', component: QuickHomeComponent },
  { path: 'doc', component: DocBaseComponent },
  { path: 'ops', component: OpsBaseComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
