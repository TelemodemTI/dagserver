export abstract class DagOpsInputPort {
    public abstract getIcons(type:string) : Promise<string>;
}  