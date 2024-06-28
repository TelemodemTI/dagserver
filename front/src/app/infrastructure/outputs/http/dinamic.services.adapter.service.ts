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

    version(){
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "version/";
            this.http.get(url,{ responseType: 'text' }).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }
    executeDagUncompiled(uncompiledId: number, dagname: string, stepname: string): Promise<any> {
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
                token: token
            };
            this.http.post(url, JSON.stringify(body), { headers: headers }).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }
}