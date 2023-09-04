import { Log } from "src/app/domain/models/log.model";

export abstract class LogsInputPort {
  public abstract removeAllLog(dagname: any):Promise<void>;
  public abstract removeLog(id: any):Promise<void>;
  public abstract logs(dagname:String):Promise<Log[]>;
}