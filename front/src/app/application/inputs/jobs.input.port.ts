import { AvailableJobs } from "src/app/domain/models/availableJobs.model";
import { ExecuteResult } from "src/app/domain/models/executeResult.model";
import { Scheduled } from "src/app/domain/models/scheduled.modem";
import { Uncompileds } from "src/app/domain/models/uncompiled.model";

export abstract class JobsInputPort {
  public abstract reimport(jarname: any): Promise<any>; 
  public abstract exportUncompiled(uncompiledId: number):Promise<any>;
  public abstract remove(jarname: any):Promise<void>;
  public abstract deleteUncompiled(uncompiledId: number):Promise<void>;
  public abstract getAvailableJobs():Promise<AvailableJobs[]>;
  public abstract getUncompileds():Promise<Uncompileds[]>;
  public abstract getScheduledJobs():Promise<Scheduled[]>;
  public abstract unscheduleDag(dagname:String, jarname: String):Promise<void>;
  public abstract scheduleDag(dagname:String, jarname: String):Promise<void>;
  public abstract executeDag(dagname:String, jarname: String, data:String):Promise<ExecuteResult>
  public abstract compile(uncompiledId: number):Promise<String>
}