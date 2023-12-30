export abstract class EncryptionOutputPort {
    public abstract set(keys:any, value:any): string;
    public abstract get_desafio(): string;
    public abstract generate_blind(password:string,desafio: string):any
}