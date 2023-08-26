import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticatedInputPort } from 'src/app/application/inputs/authenticated.input.port';
import { Agents } from 'src/app/domain/models/agent.model';

@Component({
  selector: 'app-authenticated',
  templateUrl: './authenticated.component.html',
  styleUrls: ['./authenticated.component.css']
})
export class AuthenticatedComponent {

  username:any = ""
  agents: Agents[] = []
  interval!:any
  intervalServerInfo!:any
  typeAccount!:any

  constructor(private router: Router,
    private service: AuthenticatedInputPort){}

  ngOnInit(): void {
    this.start();
    this.loadServerInfo()
    this.interval = setInterval(()=>{this.start();},3000);
    this.intervalServerInfo = setInterval(()=>{this.loadServerInfo();},60000) 
  }

  start(){
    var res = this.service.getDecodedAccessToken()
    if(!res || new Date(res.exp * 1000) < new Date()){
      this.service.removeAccessToken()
      this.router.navigateByUrl("");
    } else {
      this.username = res.username
      this.typeAccount = res.typeAccount
    }
  }

  async loadServerInfo(){
    this.agents = await this.service.getServerInfo()
  }
  
  logout():void {
    this.service.removeAccessToken()
    this.router.navigateByUrl("");
  }
  viewJobs() {
    this.router.navigateByUrl("auth/jobs");
  }
  viewProps(){
    this.router.navigateByUrl("auth/props");
  }
  viewChannels(){
    this.router.navigateByUrl("auth/channels")
  }
  credentials(){
    this.router.navigateByUrl("auth/admin/credentials");
  }
}
