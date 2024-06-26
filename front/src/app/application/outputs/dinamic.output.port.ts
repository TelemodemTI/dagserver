export abstract class DinamicOutputPort {
    public abstract version():Promise<any>
    public abstract executeDagUncompiled(uncompiledId:number,dagname:string,stepname:string) : Promise<any>
    public abstract getEntry(key:string):Promise<any>
}