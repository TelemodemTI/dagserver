export abstract class KeystoreInputPort {
  public abstract getEntries():Promise<any[]>;
  public abstract createEntry(alias:String,user:string,pwd:string):Promise<void>;
  public abstract removeEntry(alias:String):Promise<void>;
}