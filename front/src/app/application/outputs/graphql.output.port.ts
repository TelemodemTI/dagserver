import { Agents } from "src/app/domain/models/agent.model";
import { AvailableJobs } from "src/app/domain/models/availableJobs.model";
import { Detail } from "src/app/domain/models/detail.model";
import { ExecuteResult } from "src/app/domain/models/executeResult.model";
import { Log } from "src/app/domain/models/log.model";
import { Scheduled } from "src/app/domain/models/scheduled.modem";;
import { Uncompileds } from "src/app/domain/models/uncompiled.model";
import { Credential } from 'src/app/domain/models/credential.model';

export abstract class GraphQLOutputPort {
    public abstract getEntries(): Promise<any[]>;
    public abstract deleteApiKey(appname: any): Promise<void>;
    public abstract createApiKey(appname: any): Promise<void>;
    public abstract moveFile(folder:string,filename: string, newpath: any): Promise<any>;
    public abstract createCopy(filename: string, filename_copy: string): Promise<any>;
    public abstract delete(selected_folder: string, selected_file: string): Promise<any>;
    public abstract createFolder(folder: any): Promise<any>;
    public abstract mounted(): Promise<any>;
    public abstract reimport(jarname: any): Promise<any>;
    public abstract removeException(eventDt: string): Promise<void>
    public abstract getExceptions(): Promise<any[]>
    public abstract getExceptionsFromExecution(evalkey:String): Promise<any[]>
    public abstract getLastLogs(): Promise<Log[]>;
    public abstract logout(): Promise<void>;
    public abstract renameUncompiled(uncompiled: number, arg1: any): Promise<void>;
    public abstract removeLog(id: any): Promise<void>;
    public abstract removeAllLog(dagname: any): Promise<void>;
    public abstract exportUncompiled(uncompiledId: number): Promise<any>;
    public abstract removeJarfile(jarname: any): Promise<void>;
    public abstract updateProp(group: String,name: String, value: String): Promise<void>;
    public abstract getIcons(type: string): Promise<string>;
    public abstract deleteAccount(username: any): Promise<void>;
    public abstract createAccount(useracc: string, type: string, pwdHash: string): Promise<void>;
    public abstract deleteGroupProperty(name: any, group: any): Promise<void>;
    public abstract deleteUncompiled(uncompiledId: number): Promise<void>;
    public abstract login(reqobject:any):Promise<boolean>;
    public abstract operatorsMetadata(): Promise<string>
    public abstract agents(): Promise<Agents[]>;
    public abstract getAvailableJobs(): Promise<AvailableJobs[]>;
    public abstract getUncompileds(): Promise<Uncompileds[]>;
    public abstract getScheduledJobs(): Promise<Scheduled[]>;
    public abstract unscheduleDag(dagname:String, jarname: String):Promise<void> ;
    public abstract scheduleDag(dagname:String, jarname: String):Promise<void> ;
    public abstract executeDag(dagname:String, jarname: String,data:String):Promise<ExecuteResult>;
    public abstract getDetail(jarname:String):Promise<Detail>;
    public abstract createUncompiled(bin: String): Promise<void>;
    public abstract logs(dagname: String): Promise<Log[]>;
    public abstract properties():Promise<any>;
    public abstract deleteProperty(name: String, group: String):Promise<void>;
    public abstract createProperty(name: String, description: String, value: String , group: String):Promise<void>;
    public abstract saveUncompiled(uncompiledId:number,base64:String):Promise<void>;
    public abstract compile(uncompiledId: number):Promise<String>;
    public abstract getCredentials(): Promise<Credential[]>;
    public abstract updateParamsCompiled(jarname: string, idope: string,typeope:string, bin: any): Promise<void>;
    public abstract getDependencies(jarname: string,dagname:string):Promise<any>;
    public abstract createKeyEntry(alias: any,key:any,pwd:any): Promise<void>
    public abstract removeEntry(alias: any): Promise<void>
  }