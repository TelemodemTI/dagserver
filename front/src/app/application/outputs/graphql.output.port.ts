import { Agents } from "src/app/domain/models/agent.model";
import { AvailableJobs } from "src/app/domain/models/availableJobs.model";
import { Detail } from "src/app/domain/models/detail.model";
import { ExecuteResult } from "src/app/domain/models/executeResult.model";
import { Log } from "src/app/domain/models/log.model";
import { Scheduled } from "src/app/domain/models/scheduled.modem";;
import { Uncompileds } from "src/app/domain/models/uncompiled.model";
import { Credential } from 'src/app/domain/models/credential.model';

export abstract class GraphQLOutputPort {
    public abstract reimport(jarname: any): Promise<any>;
    public abstract removeException(eventDt: string): Promise<void>
    public abstract getExceptions(): Promise<any[]>
    public abstract addConsumer(topic: any, jarFile: any, dag: any): Promise<void>
    public abstract delConsumer(topic: any): Promise<void>
    public abstract saveKaflaChannel(bootstrapServers: any, groupId: any, poll: any): Promise<void>;
    public abstract getLastLogs(): Promise<Log[]>;
    public abstract logout(): Promise<void>;
    public abstract delListener(channel: string): Promise<void>;
    public abstract addListener(channel: string, jarfile: string, dagname: string): Promise<void>;
    public abstract saveRedisChannel(mode: any, hostnames: string, ports: any): Promise<void>;
    public abstract delQueue(queue: string): Promise<void>;
    public abstract addQueue(queue: string, jarfile: string, dagname: string): Promise<void>;
    public abstract saveRabbitChannel(host: string, user: string, pwd: string, port: number): Promise<void>;
    public abstract renameUncompiled(uncompiled: any, arg1: any): Promise<void>;
    public abstract removeLog(id: any): Promise<void>;
    public abstract removeAllLog(dagname: any): Promise<void>;
    public abstract exportUncompiled(uncompiledId: number): Promise<any>;
    public abstract createGithubWebhook(name:string,repourl:string,secret:string,jarname:string,dagname:string): Promise<void>;
    public abstract removeGithubWebhook(name:string): Promise<void>;
    public abstract getChannels(): Promise<any[]>;
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
    public abstract saveActiveMQChannel(host: string, user: string, pwd: string): Promise<void>;
    public abstract addConsumerAM(queue: any, jarFile: any, dag: any): Promise<void>
    public abstract delConsumerAM(queue: any): Promise<void>
  }