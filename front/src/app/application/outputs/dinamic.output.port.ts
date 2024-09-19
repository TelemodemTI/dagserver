export abstract class DinamicOutputPort {
    public abstract download(selected_folder: string, selected_file: string): Promise<any>
    public abstract version():Promise<any>
    public abstract executeDagUncompiled(uncompiledId:number,dagname:string,stepname:string) : Promise<any>
    public abstract getEntry(key:string):Promise<any>
    public abstract uploadFile(file:any,uploadPath:string): Promise<any>
    public abstract downloadKeystore(password: string): Promise<any>
}