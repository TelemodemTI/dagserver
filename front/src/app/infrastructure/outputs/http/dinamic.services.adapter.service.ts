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

    executeDagUncompiled(uncompiledId: number, dagname: string, stepname: string): Promise<any> {
        return new Promise<any>((resolve,reject)=>{
            let url = uri + "stageApi/";
            const headers = new HttpHeaders({
                'Content-Type': 'application/json' // Configura las cabeceras para enviar JSON
            });
            const body = {
                uncompiled: uncompiledId,
                dagname: dagname,
                stepname: stepname
            };
            this.http.post(url, JSON.stringify(body), { headers: headers }).subscribe((result:any)=>{
                resolve(result)
            })
        })
    }
}