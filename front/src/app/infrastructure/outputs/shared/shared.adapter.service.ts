import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { SharedOutputPort } from "src/app/application/outputs/shared.output.port";

@Injectable({
    providedIn: 'root'
})
export class SharedAdapterService implements SharedOutputPort {
    
    private executionEvent = new BehaviorSubject({});
    
    sendEventStart(data:any): Promise<void> {
        return new Promise((resolve,reject)=>{
            this.executionEvent.next(data)
            resolve();
        })
    }

    listenEvents() : any{
        return this.executionEvent.asObservable();
    }
    
}