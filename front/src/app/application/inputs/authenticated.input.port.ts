import { Agents } from "src/app/domain/models/agent.model";
import { DagToken } from "src/app/domain/models/dagtoken.model";
import { Log } from "src/app/domain/models/log.model";

export abstract class AuthenticatedInputPort {
  public abstract getLastLogs(): Promise<Log[]>;
  public abstract getDecodedAccessToken():DagToken;
  public abstract removeAccessToken():void;
  public abstract getServerInfo():Promise<Agents[]>;
  public abstract listenEvents(): any;
}