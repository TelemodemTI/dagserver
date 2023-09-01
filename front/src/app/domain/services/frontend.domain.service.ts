import { Injectable } from '@angular/core';
import { AuthenticatedInputPort } from 'src/app/application/inputs/authenticated.input.port';
import { JardetailInputPort } from 'src/app/application/inputs/jardetail.input.port';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
import { LogDetailInputPort } from 'src/app/application/inputs/logdetail.input.port';
import { LoginInputPort } from 'src/app/application/inputs/login.input.port';
import { LogsInputPort } from 'src/app/application/inputs/logs.input.port';
import { NewJInputPort } from 'src/app/application/inputs/mewj.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';
import { GraphQLOutputPort } from 'src/app/application/outputs/graphql.output.port';
import { JWTOutputPort } from 'src/app/application/outputs/jwt.output.port';
import { AvailableJobs } from '../models/availableJobs.model';
import { Credential } from 'src/app/domain/models/credential.model';
import { DagToken } from '../models/dagtoken.model';
import { Uncompileds } from '../models/uncompiled.model';
import { Scheduled } from '../models/scheduled.modem';
import { Detail } from '../models/detail.model';
import { Log } from '../models/log.model';
import { Property } from '../models/property.model';
import { ExecuteResult } from '../models/executeResult.model';
import { Agents } from '../models/agent.model';
import { ExistingJInputPort } from 'src/app/application/inputs/existingj.input.port';
import { CredentialsInputPort } from 'src/app/application/inputs/credentials.input.port';
import { EncryptionOutputPort } from 'src/app/application/outputs/encryption.output.port';
import { environment  } from 'src/environments/environment';
import { JardetailpInputPort } from 'src/app/application/inputs/jardetailp.input.port';
import { DependenciesInputPort } from 'src/app/application/inputs/dependencies.input.port';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';

@Injectable({
  providedIn: 'root'
})
export class FrontEndDomainService implements 
    LoginInputPort, 
    AuthenticatedInputPort, 
    JobsInputPort, 
    JardetailInputPort,
    NewJInputPort,
    LogsInputPort,
    LogDetailInputPort,
    PropsInputPort,
    ExistingJInputPort,
    CredentialsInputPort,
    JardetailpInputPort,
    DependenciesInputPort,
    InputsChannelsInputPort {

  constructor(private adapter: GraphQLOutputPort,
    private jwtadapter:JWTOutputPort,
    private encryptor: EncryptionOutputPort) { }
  
  exportUncompiled(uncompiledId: number): Promise<void> {
    return this.adapter.exportUncompiled(uncompiledId);
  }
  
  removeGithubWebhook(name: string): Promise<void> {
    return this.adapter.removeGithubWebhook(name);
  }

  createGithubWebhook(name:string,repourl:string,secret:string,jarname:string,dagname:string): Promise<void> {
    return this.adapter.createGithubWebhook(name,repourl,secret,jarname,dagname);
  }
  
  remove(jarname: any): Promise<void> {
    return this.adapter.removeJarfile(jarname);
  }
  
  updateProp(group: String,name: String, value: String): Promise<void> {
    return this.adapter.updateProp(group,name,value);
  }
  
  getDependencies(jarname:string,dagname:string): Promise<any[]> {
    return this.adapter.getDependencies(jarname,dagname)
  }

  getIcons(type: string): Promise<string> {
    return this.adapter.getIcons(type);
  }
  
  
  deleteAccount(username: any): Promise<void> {
    return this.adapter.deleteAccount(username);
  }
  
  
  createAccount(useracc: string, type: string, pwdHash: string): Promise<void> {
    let encrypted = this.encryptor.set(environment.sha256key,pwdHash)
    return this.adapter.createAccount(useracc,type,encrypted)
  }
  
  getCredentials(): Promise<Credential[]> {
    return this.adapter.getCredentials();
  }
  
  getOperatorMetadata(): Promise<string> {
    return this.adapter.operatorsMetadata()
  }


  deleteGroupProperty(name: any, group: any): Promise<void> {
    return this.adapter.deleteGroupProperty(name,group);
  }
  
  
  deleteUncompiled(uncompiledId: number): Promise<void> {
    return this.adapter.deleteUncompiled(uncompiledId);
  }

  compile(uncompiledId: number): Promise<String> {
    return this.adapter.compile(uncompiledId);
  }
  
  saveUncompiled(uncompiledId: number, base64: String): Promise<void> {
    return this.adapter.saveUncompiled(uncompiledId,base64);
  }

  createProperty(name: String, description: String, value: String, group: String): Promise<void> {
    return this.adapter.createProperty(name,description,value,group);
  }
  deleteProperty(name: String, group: String): Promise<void> {
    return this.adapter.deleteProperty(name,group);
  }
  properties(): Promise<Property[]> {
    return this.adapter.properties()
  }
  logs(dagname: String): Promise<Log[]> {
    return this.adapter.logs(dagname);
  }
  createUncompiled(bin: String): Promise<void> {
    return this.adapter.createUncompiled(bin);
  }
  getDetail(jarname: String): Promise<Detail> {
    return this.adapter.getDetail(jarname);
  }
  executeDag(dagname: String, jarname: String): Promise<ExecuteResult> {
    return this.adapter.executeDag(dagname,jarname);
  }
  unscheduleDag(dagname: String, jarname: String): Promise<void> {
    return this.adapter.unscheduleDag(dagname,jarname);
  }
  scheduleDag(dagname: String, jarname: String): Promise<void> {
    return this.adapter.scheduleDag(dagname,jarname);
  }
  getAvailableJobs(): Promise<AvailableJobs[]> {
    return this.adapter.getAvailableJobs()
  }
  getUncompileds(): Promise<Uncompileds[]> {
    return this.adapter.getUncompileds();
  }
  getScheduledJobs(): Promise<Scheduled[]> {
    return this.adapter.getScheduledJobs();
  }
  getServerInfo(): Promise<Agents[]> {
    return this.adapter.agents();
  }
  getDecodedAccessToken(): DagToken {
    return this.jwtadapter.getDecodedAccessToken();
  }
  removeAccessToken(): void {
    this.jwtadapter.removeAccessToken();
  }
  login(user: any, pwd: any): Promise<boolean> {
    let encrypted = this.encryptor.set(environment.sha256key,pwd)
    return this.adapter.login(user,encrypted);
  }
  updateParamsCompiled(jarname: string, idope: string,typeope:string, bin: any): Promise<void> {
    return this.adapter.updateParamsCompiled(jarname,idope,typeope,bin);
  }
  getChannels(): Promise<any[]> {
    return this.adapter.getChannels();
  }
}
