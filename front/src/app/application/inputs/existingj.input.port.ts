import { Uncompileds } from "src/app/domain/models/uncompiled.model";

export abstract class ExistingJInputPort {
  public abstract getUncompileds():Promise<Uncompileds[]>;
  public abstract saveUncompiled(uncompiledId:number,base64:String):Promise<void>;
  public abstract getOperatorMetadata():Promise<string>;
  public abstract getIcons(type:string) : Promise<string>;
}