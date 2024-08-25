import { Credential } from 'src/app/domain/models/credential.model';
export abstract class ExplorerInputPort {
    public abstract getMounted():Promise<any>;
}