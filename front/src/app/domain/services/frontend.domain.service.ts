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
import { DinamicOutputPort } from 'src/app/application/outputs/dinamic.output.port';
import { SharedOutputPort } from 'src/app/application/outputs/shared.output.port';
import { ExplorerInputPort } from 'src/app/application/inputs/explorer.input.port';
import { KeystoreInputPort } from 'src/app/application/inputs/keystore.input.port';

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
    KeystoreInputPort,
    ExistingJInputPort,
    CredentialsInputPort,
    JardetailpInputPort,
    DependenciesInputPort,
    InputsChannelsInputPort,
    ExplorerInputPort {

  constructor(private adapter: GraphQLOutputPort,
    private httpd: DinamicOutputPort,
    private jwtadapter:JWTOutputPort,
    private shared:SharedOutputPort,
    private dinamic:DinamicOutputPort,
    private encryptor: EncryptionOutputPort) { }

  uploadKeystore(file: any): Promise<any> {
    return this.httpd.uploadFile(file,"","upload-keystore");
  }
  downloadKeystore(): Promise<any> {
    return this.dinamic.downloadKeystore();
  }
  
  createEntry(alias: String, user: string, pwd: string): Promise<void> {
    return this.adapter.createKeyEntry(alias,user,pwd);
  }
  removeEntry(alias: String): Promise<void> {
    return this.adapter.removeEntry(alias)
  }
  
  getEntries(): Promise<any[]> {
    return this.adapter.getEntries();
  }

  deleteApiKey(appname: any): Promise<void> {
    return this.adapter.deleteApiKey(appname)
  }
  
  createApiKey(appname: any): Promise<void> {
    return this.adapter.createApiKey(appname);
  }
  
    move(folder:string,filename: string, newpath: any): Promise<any> {
    return this.adapter.moveFile(folder,filename, newpath);
  }
  
    createCopy(filename: string,filename_copy:string) {
      return this.adapter.createCopy(filename,filename_copy)
    }

  download(selected_folder: string, selected_file: string): Promise<any> {
    return this.httpd.download(selected_folder,selected_file);
  }

  delete(selected_folder: string, selected_file: string): Promise<any> {
    return this.adapter.delete(selected_folder,selected_file);
  }
  
  createFolder(folder: any): Promise<any> {
    return this.adapter.createFolder(folder);
  }
  
  
  uploadMounted(file: File,inputPath:string): Promise<any> {
    return this.httpd.uploadFile(file,inputPath,"explorer/upload-file");
  }
  
  getMounted(): Promise<any> {
    return this.adapter.mounted();
  }
  
  
  reimport(jarname: any): Promise<any> {
    return this.adapter.reimport(jarname);
  }
  
  getEntry(key: string): Promise<any> {
    return this.httpd.getEntry(key);
  }

  
  getLastLogs(): Promise<Log[]> {
    return this.adapter.getLastLogs();
  }

  version(): Promise<any> {
    return this.httpd.version()
  }
  
  
  executeDagUncompiled(uncompiledId: number, dagname: string, stepname: string, args:string): Promise<any> {
    return this.httpd.executeDagUncompiled(uncompiledId,dagname,stepname,args)
  }
  
  renameUncompiled(uncompiled: number, arg1: any): Promise<void> {
    return this.adapter.renameUncompiled(uncompiled,arg1);
  }
  
  removeAllLog(dagname: any): Promise<void> {
    return this.adapter.removeAllLog(dagname);
  }
  removeLog(id: any): Promise<void> {
    return this.adapter.removeLog(id);
  }
  exportUncompiled(uncompiledId: number): Promise<void> {
    return this.adapter.exportUncompiled(uncompiledId);
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
  executeDag(dagname: String, jarname: String, data:String): Promise<ExecuteResult> {
    return this.adapter.executeDag(dagname,jarname,data);
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
  logout(): void {
	  this.adapter.logout()
    this.jwtadapter.removeAccessToken();
  }
  login(user: any, pwd: any): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.adapter.properties().then((props: Property[]) => {
        let loginType = "interno";
        for (let index = 0; index < props.length; index++) {
          const element = props[index];
          if (element.name == "auth-keycloak") {
            loginType = "keycloak";
            break;
          }
        }
        if (loginType == "interno") {
          this.loginIncluded(user, pwd).then(resolve).catch(reject); // Devuelve la promesa de loginIncluded
        } else {
          this.loginKeyCloak(user, pwd).then(resolve).catch(reject); // Devuelve la promesa de loginKeyCloak
        }
      }).catch(reject); // En caso de error en la obtención de propiedades
    });
  }
  loginKeyCloak(user: any, pwd: any){
    let requestObj = { username: user , challenge:pwd, mode: "keycloak" }
    return this.adapter.login(requestObj);
  }
  loginIncluded(user: any, pwd: any){
    let desafiostr = this.encryptor.get_desafio();
    let blindFirm = this.encryptor.generate_blind(pwd,desafiostr)
    let requestObj = { username: user , challenge:desafiostr, mode: "included" }
    Object.assign(requestObj,blindFirm)
    return this.adapter.login(requestObj);
  }
  updateParamsCompiled(jarname: string, idope: string,typeope:string, bin: any): Promise<void> {
    return this.adapter.updateParamsCompiled(jarname,idope,typeope,bin);
  }
  
  sendResultExecution(data:any): Promise<void>{
    return this.shared.sendEventStart(data);
  }
  listenEvents(): any {
    return this.shared.listenEvents();
  }
  getExceptions() {
    return this.adapter.getExceptions();
  }
  removeException(eventDt:string){
    return this.adapter.removeException(eventDt);
  }
}
