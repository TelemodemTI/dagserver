import { Agents } from "src/app/domain/models/agent.model";
import { AvailableJobs } from "src/app/domain/models/availableJobs.model";
import { Detail } from "src/app/domain/models/detail.model";
import { ExecuteResult } from "src/app/domain/models/executeResult.model";
import { Log } from "src/app/domain/models/log.model";
import { Scheduled } from "src/app/domain/models/scheduled.modem";;
import { Uncompileds } from "src/app/domain/models/uncompiled.model";
import { Credential } from 'src/app/domain/models/credential.model';

export abstract class GraphQLOutputPort {
    
    public abstract getIcons(type: string): Promise<string>;
    public abstract deleteAccount(username: any): Promise<void>;
    public abstract createAccount(useracc: string, type: string, pwdHash: string): Promise<void>;
    public abstract deleteGroupProperty(name: any, group: any): Promise<void>;
    public abstract deleteUncompiled(uncompiledId: number): Promise<void>;
    public abstract login(user:any,pwd:any):Promise<boolean>;
    public abstract operatorsMetadata(): Promise<string>
    public abstract agents(): Promise<Agents[]>;
    public abstract getAvailableJobs(): Promise<AvailableJobs[]>;
    public abstract getUncompileds(): Promise<Uncompileds[]>;
    public abstract getScheduledJobs(): Promise<Scheduled[]>;
    public abstract unscheduleDag(dagname:String, jarname: String):Promise<void> ;
    public abstract scheduleDag(dagname:String, jarname: String):Promise<void> ;
    public abstract executeDag(dagname:String, jarname: String):Promise<ExecuteResult>;
    public abstract getDetail(jarname:String):Promise<Detail>;
    public abstract createUncompiled(bin: String): Promise<void>;
    public abstract logs(dagname: String): Promise<Log[]>;
    public abstract properties():Promise<any>;
    public abstract deleteProperty(name: String, group: String):Promise<void>;
    public abstract createProperty(name: String, description: String, value: String , group: String):Promise<void>;
    public abstract saveUncompiled(uncompiledId:number,base64:String):Promise<void>;
    public abstract compile(uncompiledId: number):Promise<String>;
    public abstract getCredentials(): Promise<Credential[]>;
  }