
export abstract class LoginInputPort {
  public abstract login(user:any,pwd:any):Promise<boolean>;
}