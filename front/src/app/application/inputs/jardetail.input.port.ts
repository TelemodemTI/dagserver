import { Detail } from "src/app/domain/models/detail.model";

export abstract class JardetailInputPort {
  public abstract getDetail(jarname:String):Promise<Detail>;
  public abstract getIcons(type:string) : Promise<string>;
}