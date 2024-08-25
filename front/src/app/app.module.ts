import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GraphQLModule } from './graphql.module';
import { HttpClientModule } from '@angular/common/http';
import { BaseModule } from './infrastructure/inputs/base/base.module';
import { JobsModule } from './infrastructure/inputs/jobs/jobs.module';
import { LogsModule } from './infrastructure/inputs/logs/logs.module';
import { PropsModule } from './infrastructure/inputs/props/props.module';
import { LoginInputPort } from './application/inputs/login.input.port';
import { FrontEndDomainService } from './domain/services/frontend.domain.service';
import { GraphQLOutputPort } from './application/outputs/graphql.output.port';
import { GraphQLOutputPortAdapterService } from './infrastructure/outputs/graphql/graphql.output.adapter.service';
import { JWTOutputPort } from './application/outputs/jwt.output.port';
import { JwtOutputPortAdapterService } from './infrastructure/outputs/jwt/jwt.output.adapter.service';
import { AuthenticatedInputPort } from './application/inputs/authenticated.input.port';
import { JardetailInputPort } from './application/inputs/jardetail.input.port';
import { JobsInputPort } from './application/inputs/jobs.input.port';
import { LogDetailInputPort } from './application/inputs/logdetail.input.port';
import { LogsInputPort } from './application/inputs/logs.input.port';
import { NewJInputPort } from './application/inputs/mewj.input.port';
import { PropsInputPort } from './application/inputs/props.input.port';
import { ExistingJInputPort } from './application/inputs/existingj.input.port';
import { CredentialsModule } from './infrastructure/inputs/credentials/credentials.module';
import { CredentialsInputPort } from './application/inputs/credentials.input.port';
import { EncryptionOutputPort } from './application/outputs/encryption.output.port';
import { EncryptionOutputPortAdapterService } from './infrastructure/outputs/encryption/encryption.output.adapter.service';
import { DagOpsInputPort } from './application/inputs/dagops.input.port';
import { DagPropsInputPort } from './application/inputs/dagprops.input.port';
import { JardetailpInputPort } from './application/inputs/jardetailp.input.port';
import { APP_BASE_HREF } from '@angular/common';
import { DependenciesInputPort } from './application/inputs/dependencies.input.port';
import { InputsChannelsInputPort } from './application/inputs/inputschannels.input.port';
import { InputsModule } from './infrastructure/inputs/inputs/inputs.module';
import { DinamicOutputPort } from './application/outputs/dinamic.output.port';
import { DinamicAdapterService } from './infrastructure/outputs/http/dinamic.services.adapter.service';
import { SharedOutputPort } from './application/outputs/shared.output.port';
import { SharedAdapterService } from './infrastructure/outputs/shared/shared.adapter.service';
import { ExceptionsModule } from './infrastructure/inputs/exceptions/exceptions.module';
import { ExceptionsInputPort } from './application/inputs/exceptions.input.port';
import { ParamEditorModule } from './infrastructure/inputs/param-editor/param-editor.module';
import { ExplorerInputPort } from './application/inputs/explorer.input.port';


declare var window: any;
@NgModule({
  declarations: [
    AppComponent
  ],

  imports: [
    BrowserModule,
    AppRoutingModule,
    GraphQLModule,
    HttpClientModule,
    BaseModule,
    JobsModule,
    LogsModule,
    PropsModule,
    CredentialsModule,
    InputsModule,
    ExceptionsModule,
    ParamEditorModule,
    BrowserModule
  ],
  providers: [
    { provide: APP_BASE_HREF, useValue: window["base-href"]},
    { provide: GraphQLOutputPort , useClass: GraphQLOutputPortAdapterService },
    { provide: JWTOutputPort, useClass: JwtOutputPortAdapterService },
    { provide: DinamicOutputPort, useClass: DinamicAdapterService },
    { provide: EncryptionOutputPort, useClass: EncryptionOutputPortAdapterService },
    { provide: SharedOutputPort, useClass: SharedAdapterService },
    { provide: LoginInputPort , useClass: FrontEndDomainService },
    { provide: AuthenticatedInputPort, useClass: FrontEndDomainService},
    { provide: DagOpsInputPort, useClass: FrontEndDomainService},
    { provide: DagPropsInputPort, useClass: FrontEndDomainService},
    { provide: JardetailInputPort, useClass: FrontEndDomainService},
    { provide: JobsInputPort, useClass: FrontEndDomainService},
    { provide: LogDetailInputPort, useClass: FrontEndDomainService},
    { provide: LoginInputPort, useClass: FrontEndDomainService},
    { provide: LogsInputPort, useClass: FrontEndDomainService},
    { provide: NewJInputPort, useClass: FrontEndDomainService},
    { provide: PropsInputPort, useClass: FrontEndDomainService},
    { provide: ExistingJInputPort, useClass: FrontEndDomainService},
    { provide: CredentialsInputPort, useClass: FrontEndDomainService},
    { provide: JardetailpInputPort, useClass: FrontEndDomainService},
    { provide: DependenciesInputPort, useClass: FrontEndDomainService},
    { provide: InputsChannelsInputPort, useClass: FrontEndDomainService },
    { provide: ExceptionsInputPort, useClass: FrontEndDomainService },
    { provide: ExplorerInputPort, useClass: FrontEndDomainService }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
