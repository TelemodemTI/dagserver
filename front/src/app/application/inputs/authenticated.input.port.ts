import { Agents } from "src/app/domain/models/agent.model";
import { DagToken } from "src/app/domain/models/dagtoken.model";

export abstract class AuthenticatedInputPort {
  public abstract getDecodedAccessToken():DagToken;
  public abstract removeAccessToken():void;
  public abstract getServerInfo():Promise<Agents[]>;
}