import { AvailableJobs } from "src/app/domain/models/availableJobs.model";
import { ExecuteResult } from "src/app/domain/models/executeResult.model";
import { Scheduled } from "src/app/domain/models/scheduled.modem";
import { Uncompileds } from "src/app/domain/models/uncompiled.model";

export abstract class DependenciesInputPort {
  public abstract getDependencies(jarname:string,dagname:string):Promise<any>;
}