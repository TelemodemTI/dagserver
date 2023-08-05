import { Injectable } from '@angular/core';
import { JWTOutputPort } from 'src/app/application/outputs/jwt.output.port';
import { DagToken } from '../../../domain/models/dagtoken.model';
import jwt_decode from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class JwtOutputPortAdapterService implements JWTOutputPort {

  constructor() { }
  getDecodedAccessToken(): DagToken {
    var token = localStorage.getItem("dagserver_token")!
    var decoded = jwt_decode<DagToken>(token)
    return decoded
  }
  removeAccessToken(): void {
    localStorage.removeItem("dagserver_token");
  }
  
  
}
