
export abstract class BrowserInputPort {
  public abstract getXcomKeys(): Promise<any[]>;
  public abstract getEntry(key:string): Promise<any>;
}