
export abstract class PropsInputPort {
  public abstract deleteGroupProperty(name: any, group: any):Promise<void>
  public abstract properties():Promise<any>;
  public abstract deleteProperty(name: String, group: String):Promise<void>
  public abstract createProperty(name: String, description: String, value: String , group: String):Promise<void>
  public abstract updateProp(group: String,name:String,value:String): Promise<void>
}