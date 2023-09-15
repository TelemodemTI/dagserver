export abstract class DinamicOutputPort {
    public abstract executeDagUncompiled(uncompiledId:number,dagname:string,stepname:string) : Promise<any>
}