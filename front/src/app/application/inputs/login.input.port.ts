
export abstract class LoginInputPort {
  public abstract version(): Promise<any>;
  public abstract login(user:any,pwd:any):Promise<boolean>;
}