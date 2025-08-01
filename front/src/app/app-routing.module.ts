import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticatedComponent } from './infrastructure/inputs/base/authenticated/authenticated.component';
import { HomeComponent } from './infrastructure/inputs/base/home/home.component';
import { JobsComponent } from './infrastructure/inputs/jobs/jobs/jobs.component';
import { LoginComponent } from './infrastructure/inputs/base/login/login.component';

import { JardetailComponent } from './infrastructure/inputs/jobs/jardetail/jardetail.component';
import { PropsComponent } from './infrastructure/inputs/props/props/props.component';
import { LogdetailComponent } from './infrastructure/inputs/logs/logdetail/logdetail.component';
import { NewjComponent } from './infrastructure/inputs/jobs/newj/newj.component';
import { ExistingjComponent } from './infrastructure/inputs/jobs/existingj/existingj.component';
import { LogsComponent } from './infrastructure/inputs/logs/logs/logs.component';
import { CredentialsComponent } from './infrastructure/inputs/credentials/credentials/credentials.component';
import { DependenciesComponent } from './infrastructure/inputs/jobs/dependencies/dependencies.component';
import { ExceptionsListComponent } from './infrastructure/inputs/exceptions/exceptions-list/exceptions-list.component';
import { ExplorerComponent } from './infrastructure/inputs/browser-fs/explorer/explorer.component';
import { KeystoreContentComponent } from './infrastructure/inputs/keystore/keystore-content/keystore-content.component';
import { ApiKeyComponent } from './infrastructure/inputs/http-api-security/api-key/api-key.component';


const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'auth', component: AuthenticatedComponent, children: [
    { path: '', component: HomeComponent },
    { path: 'jobs', component: JobsComponent },
    { path: 'api-keys', component: ApiKeyComponent },
    { path: 'props', component: PropsComponent },
    { path: "njob" , component: NewjComponent },
    { path: 'browser' , component: ExplorerComponent },
    { path: "exceptions", component: ExceptionsListComponent },
    { path: "keystore", component: KeystoreContentComponent },
    { path: "admin/credentials", component: CredentialsComponent },
    { path: "njob/:uncompiledId" , component: ExistingjComponent },
    { path: 'jobs/jarname/:jarname/:dagname', component: JardetailComponent },
    { path: 'jobs/:dagname', component: LogsComponent },
    { path: 'jobs/:dagname/:logid', component: LogdetailComponent },
    { path: 'dependencies/:jarname/:dagname', component: DependenciesComponent },
  ] },
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
