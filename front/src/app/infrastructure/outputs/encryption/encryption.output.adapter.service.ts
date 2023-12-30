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
    
    public get_desafio(){
      return CryptoJS.lib.WordArray.random(16).toString();
    }

    public generate_blind(password:string,desafio: string){ 
      const passwordHash = CryptoJS.SHA256(password).toString();
      const privateKey = CryptoJS.lib.WordArray.random(16).toString();
      const publicKey = CryptoJS.SHA256(passwordHash+privateKey).toString();
      const challengeHash = CryptoJS.SHA256(desafio).toString();
      const blindSignature = CryptoJS.SHA256(publicKey + challengeHash).toString();
      return {
        private_key: privateKey,
        blind_signature: blindSignature,
      };
    }
  }