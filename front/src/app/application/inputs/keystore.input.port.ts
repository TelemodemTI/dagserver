export abstract class KeystoreInputPort {
  public abstract getEntries():Promise<any[]>;
}