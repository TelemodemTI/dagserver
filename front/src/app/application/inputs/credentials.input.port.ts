import { Credential } from 'src/app/domain/models/credential.model';
export abstract class CredentialsInputPort {
    public abstract deleteAccount(username: any):Promise<void>;
    public abstract createAccount(useracc:string,type:string,pwdHash:string):Promise<void>;
    public abstract getCredentials():Promise<Credential[]>;
}