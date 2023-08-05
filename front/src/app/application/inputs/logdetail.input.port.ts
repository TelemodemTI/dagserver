import { Log } from "src/app/domain/models/log.model";

export abstract class LogDetailInputPort {
  public abstract logs(dagname:String):Promise<Log[]>;
}