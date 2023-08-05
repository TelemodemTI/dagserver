
export abstract class NewJInputPort {
  public abstract createUncompiled(bin:String):Promise<void>;
  public abstract getOperatorMetadata():Promise<string>;
}