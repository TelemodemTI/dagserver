import { Credential } from 'src/app/domain/models/credential.model';
export abstract class ExplorerInputPort {
    public abstract move(folder:string,filename: string, newpath: any):Promise<any>
    public abstract createCopy(filename: string,copyfilename:string):Promise<any>
    public abstract download(selected_folder: string, selected_file: string):Promise<any>
    public abstract delete(selected_folder: string, selected_file: string):Promise<any>
    public abstract createFolder(folder: any):Promise<any>
    public abstract uploadMounted(file: File,inputPath:string):Promise<any>;
    public abstract getMounted():Promise<any>;
}