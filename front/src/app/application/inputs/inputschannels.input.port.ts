export abstract class InputsChannelsInputPort {
    public abstract removeGithubWebhook(name:string):Promise<void>;
    public abstract createGithubWebhook(name:string,repourl:string,secret:string,jarname:string,dagname:string):Promise<void>;
    public abstract getChannels():Promise<any[]>;
}