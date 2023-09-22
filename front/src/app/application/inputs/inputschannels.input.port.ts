export abstract class InputsChannelsInputPort {
    public abstract removeGithubWebhook(name:string):Promise<void>;
    public abstract createGithubWebhook(name:string,repourl:string,secret:string,jarname:string,dagname:string):Promise<void>;
    public abstract getChannels():Promise<any[]>;
    public abstract saveRabbitChannel(host:string, user:string, pwd:string, port:number):Promise<void>;
    public abstract addQueue(queue:string,jarfile:string,dagname:string):Promise<void>;
	public abstract delQueue(queue:string):Promise<void>;
}