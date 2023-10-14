import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticatedInputPort } from 'src/app/application/inputs/authenticated.input.port';
import { Agents } from 'src/app/domain/models/agent.model';
import { ResultStepModalComponent } from '../result-step-modal/result-step-modal.component';
declare var window:any
declare var environment : any;
const uri =  environment.dagserverUri;
@Component({
  selector: 'app-authenticated',
  templateUrl: './authenticated.component.html',
  styleUrls: ['./authenticated.component.css']
})
export class AuthenticatedComponent {

  @ViewChild("resultStepModalAut") resultStepModalAut!:ResultStepModalComponent

  username:any = ""
  agents: Agents[] = []
  interval!:any
  intervalServerInfo!:any
  typeAccount!:any
  notifications:any[] = []
  logs:any[] = []
  badget:number = 0

  constructor(private router: Router,
    private service: AuthenticatedInputPort){}

  ngOnInit(): void {
    this.start();
    this.loadServerInfo()
    this.interval = setInterval(()=>{this.start();},3000);
    this.intervalServerInfo = setInterval(()=>{
      this.loadServerInfo();
      this.loadLastLogs();
    },15000) 
    
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
  async loadLastLogs(){
    this.logs = await this.service.getLastLogs();
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
  reset(){
    this.badget = 0
  }  
  result(item:any){
    this.resultStepModalAut.show(item);
  }
  resultLog(item:any){
    this.router.navigateByUrl(`auth/jobs/${item.dagname}/${item.id}`);
  }
  async goToDocs(){
    window.location.href = "https://docs.telemodem.cl/books/dagserver-documentation"
  }
  async goToMonitor(){
    let url = uri + "monitoring";
    window.location.href = url
  }
}
