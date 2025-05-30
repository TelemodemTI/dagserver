export abstract class InputsChannelsInputPort {
    public abstract deleteApiKey(key: any):Promise<void>
    public abstract createApiKey(value: any):Promise<void>
    public abstract delQueueAM(queue: any):Promise<void>
    public abstract addQueueAM(queue: any, jarFile: any, dag: any):Promise<void>
    public abstract delConsumer(channel: any):Promise<void>
    public abstract addConsumer(topic: any, jarFile: any, dag: any):Promise<void>
    public abstract saveKafkaChannel(bootstrapServers: any, groupId: any, poll: any):Promise<void>;
    public abstract saveRedisChannel(mode: any, hosts: string, ports: string):Promise<void>;
    public abstract addListener(channel:string,jarfile:string,dagname:string):Promise<void>;
	public abstract delListener(channel:string):Promise<void>;
    public abstract removeGithubWebhook(name:string):Promise<void>;
    public abstract createGithubWebhook(name:string,repourl:string,secret:string,jarname:string,dagname:string):Promise<void>;
    public abstract getChannels():Promise<any[]>;
    public abstract saveRabbitChannel(host:string, cred:string, port:number):Promise<void>;
    public abstract addQueue(queue:string,jarfile:string,dagname:string):Promise<void>;
	public abstract delQueue(queue:string):Promise<void>;
    public abstract saveActiveMQChannel(host:string, cred:string):Promise<void>;
}