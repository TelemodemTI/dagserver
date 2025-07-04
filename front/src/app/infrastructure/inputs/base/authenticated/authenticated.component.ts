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

    setTimeout(()=>{
      this.loadScript("/assets/js/startmin.js",function(){});
    },100)
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
      this.service.logout()
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
    this.service.logout();
    clearInterval(this.interval)
    this.router.navigateByUrl("");
  }
  viewJobs() {
    this.router.navigateByUrl("auth/jobs");
  }
  viewBrowser(){
    this.router.navigateByUrl("auth/browser");
  }
  viewProps(){
    this.router.navigateByUrl("auth/props");
  }
  viewExceptions(){
    this.router.navigateByUrl("auth/exceptions")
  }
  viewKeystore(){
    this.router.navigateByUrl("auth/keystore")
  }
  viewBeans(){
    window.location.href = "/beans"
  }
  viewGraphi(){
    window.location.href = "/graphiql"
  }
  viewOpenApiJSON(){
    window.location.href = "/swagger-ui/index.html"
  }
  credentials(){
    this.router.navigateByUrl("auth/admin/credentials");
  }
  viewApikeys(){
    this.router.navigateByUrl("auth/api-keys");
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
  async goToMonitor(){
    let url = uri + "monitoring";
    window.location.href = url
  }
  loadScript(src:any, callback:any,namespace?:any){
    var s:any,
        r:any,
        t:any;
    r = false;
    s = document.createElement('script');

    if(namespace){
      s.setAttribute("data-namespace",namespace)
    }
    
    s.type = 'text/javascript';
    s.src = src;
    s.onload = s.onreadystatechange = function() {
      if ( !r && (!this.readyState || this.readyState == 'complete') )
      {
        r = true;
        callback();
      }
    };
    t = document.getElementsByTagName('script')[0];
    t.parentNode.insertBefore(s, t);
  }

}
