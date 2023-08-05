import { Injectable } from "@angular/core";
import { EncryptionOutputPort } from "src/app/application/outputs/encryption.output.port";
import * as CryptoJS from 'crypto-js';


@Injectable({
    providedIn: 'root'
  })
  export class EncryptionOutputPortAdapterService implements EncryptionOutputPort {
  
    
    public set(keys:any, value:any){        
            let res = CryptoJS.SHA256(value,keys);
            return  res.toString();
    }
    
  }