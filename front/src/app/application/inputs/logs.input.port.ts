import { Log } from "src/app/domain/models/log.model";

export abstract class LogsInputPort {
  public abstract logs(dagname:String):Promise<Log[]>;
}