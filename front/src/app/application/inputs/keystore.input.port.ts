export abstract class KeystoreInputPort {
  public abstract uploadKeystore(file: any) :Promise<any>;
  public abstract downloadKeystore():Promise<void>;
  public abstract getEntries():Promise<any[]>;
  public abstract createEntry(alias:String,user:string,pwd:string):Promise<void>;
  public abstract removeEntry(alias:String):Promise<void>;
}