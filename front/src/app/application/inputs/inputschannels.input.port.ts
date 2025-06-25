export abstract class InputsChannelsInputPort {
    public abstract deleteApiKey(key: any):Promise<void>
    public abstract createApiKey(value: any):Promise<void>
}