import { Credential } from 'src/app/domain/models/credential.model';
export abstract class JardetailpInputPort {
    public abstract updateParamsCompiled(jarname: string,idope: string,typeope:string, bin:any):Promise<void>;
    
}