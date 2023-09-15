
export abstract class SharedOutputPort {
    public abstract sendEventStart(data:any) : Promise<void>
    public abstract listenEvents(): any
}