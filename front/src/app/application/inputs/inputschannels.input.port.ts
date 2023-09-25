export abstract class InputsChannelsInputPort {
    public abstract saveRedisChannel(mode: any, hotsport: string, channel: any, jarFile: any, dagname: any):Promise<void>;
    public abstract removeGithubWebhook(name:string):Promise<void>;
    public abstract createGithubWebhook(name:string,repourl:string,secret:string,jarname:string,dagname:string):Promise<void>;
    public abstract getChannels():Promise<any[]>;
    public abstract saveRabbitChannel(host:string, user:string, pwd:string, port:number):Promise<void>;
    public abstract addQueue(queue:string,jarfile:string,dagname:string):Promise<void>;
	public abstract delQueue(queue:string):Promise<void>;
}