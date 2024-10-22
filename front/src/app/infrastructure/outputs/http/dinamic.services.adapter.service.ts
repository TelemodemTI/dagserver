import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { DinamicOutputPort } from "src/app/application/outputs/dinamic.output.port";
declare var environment : any;
const uri =  environment.dagserverUri;
@Injectable({
    providedIn: 'root'
})
export class DinamicAdapterService implements DinamicOutputPort {
    
    constructor(private http: HttpClient) {}

   

    download(selected_folder: string, selected_file: string): Promise<any> {
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "explorer/download-file";
            var token = localStorage.getItem("dagserver_token")!
            const params = new HttpParams()
                .set('token', token)
                .set('folder', selected_folder)
                .set('file', selected_file);
        
            this.http.get(url, { params: params, responseType: 'blob' }).subscribe((response: any) => {
                // Crea un enlace temporal para descargar el archivo
                const blob = new Blob([response], { type: response.type });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = selected_file;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                resolve(true);
            }, (error: any) => {
                reject(error);
            });
        })
    }
    
    getEntry(key: string): Promise<any> {
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "xcombrowser/";
            const headers = new HttpHeaders({
                'Content-Type': 'application/json' // Configura las cabeceras para enviar JSON
            });
            var token = localStorage.getItem("dagserver_token")
            const body = {
                xcomkey:key,
                token: token
            };
            this.http.post(url, JSON.stringify(body), { headers: headers }).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }

    uploadFile(file:any,uploadPath:string,endpoint:string): Promise<any> {
        return new Promise<any>((resolve,reject)=>{
            var token = localStorage.getItem("dagserver_token")!
            const formData = new FormData();
            formData.append('file', file);
            formData.append("upload-path",uploadPath);
            formData.append("token",token);
            let url = uri + endpoint;
            this.http.post(url, formData).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }


    version(){
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "version/";
            this.http.get(url,{ responseType: 'text' }).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }
    executeDagUncompiled(uncompiledId: number, dagname: string, stepname: string, args:string): Promise<any> {
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "stageApi/";
            const headers = new HttpHeaders({
                'Content-Type': 'application/json' // Configura las cabeceras para enviar JSON
            });
            var token = localStorage.getItem("dagserver_token")
            const body = {
                uncompiled: uncompiledId,
                dagname: dagname,
                stepname: stepname,
                token: token,
                args: args,
            };
            this.http.post(url, JSON.stringify(body), { headers: headers }).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }


    downloadKeystore(): Promise<any> {
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "download-keystore";
            var token = localStorage.getItem("dagserver_token")!
            const params = new HttpParams()
                .set('token', token);

            this.http.get(url, { params: params, responseType: 'blob' }).subscribe((response: any) => {
                // Crea un enlace temporal para descargar el archivo
                const blob = new Blob([response], { type: response.type });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = "keystore.jks";
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                resolve(true);
            }, (error: any) => {
                reject(error);
            });
        })
    }
    
}