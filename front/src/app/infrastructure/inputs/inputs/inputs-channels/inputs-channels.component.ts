import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';
import { AvailableJobs } from 'src/app/domain/models/availableJobs.model';
declare var $:any;
@Component({
  selector: 'app-inputs-channels',
  templateUrl: './inputs-channels.component.html',
  styleUrls: ['./inputs-channels.component.css']
})
export class InputsChannelsComponent {

  items:any[] = []
  propsSelected:any[] =[]
  jobs:AvailableJobs[] = []
  jars:any[] = []
  dags:any[] = []
  rbuser!:any
  rbpwd!:any
  rbhost!:any
  rbport!:any
  queues:any[] = []
  
  @ViewChild("reponame") reponame!:ElementRef;
  @ViewChild("repourl") repourl!:ElementRef;
  @ViewChild("reposecret") reposecret!:ElementRef;
  @ViewChild("jarfile") jarfile!:ElementRef;
  @ViewChild("dagname") dagname!:ElementRef;

  @ViewChild("rabbithost") rabbithost!:ElementRef;
  @ViewChild("rabbitport") rabbitport!:ElementRef;
  @ViewChild("rabbituser") rabbituser!:ElementRef;
  @ViewChild("rabbitpwd") rabbitpwd!:ElementRef;
  @ViewChild("rabbitqueue") rabbitqueue!:ElementRef;
  @ViewChild("jarfiler") jarfiler!:ElementRef;
  @ViewChild("dagnamer") dagnamer!:ElementRef;

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service2: JobsInputPort,
    private service3: PropsInputPort){
  }

  async ngOnInit() {
    this.items = []
    this.items = await this.service.getChannels()
    this.jobs = await this.service2.getAvailableJobs()
    let jarsf = this.jobs.map((eleme:any)=>{return eleme.jarname })
    let props = await this.service3.properties()
    let propsrabbit = props.filter((ele:any)=>{ return ele.group == 'RABBIT_PROPS' })
    for (let index = 0; index < propsrabbit.length; index++) {
      const element = propsrabbit[index];
      this.rbuser = (element.name == "username")?element.value:this.rbuser
      this.rbpwd = (element.name == "password")?"******":this.rbpwd
      this.rbhost = (element.name == "host")?element.value:this.rbhost
      this.rbport = (element.name == "port")?element.value:this.rbport
      if(element.value == "rabbit_consumer_queue"){
        let dagname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "dagname" })[0].value
        let jarname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "jarname" })[0].value
        this.queues.push({queue:element.name,dagname:dagname,jarname:jarname})
      }
    }
    console.log(this.queues)
    this.jars = [...new Set(jarsf)];
  }
  options(item:any){
    if(item.name == "GITHUB_CHANNEL"){
      $("#githubModal").modal('show');
    } else {
      $("#rabbitModal").modal('show');
    }
    this.propsSelected = item.props
  }
  async createGithubWebhook(){
    let name = this.reponame.nativeElement.value
    let repourl = this.repourl.nativeElement.value
    let secret = this.reposecret.nativeElement.value
    let jarfile = this.jarfile.nativeElement.value
    let dagname = this.dagname.nativeElement.value
    await this.service.createGithubWebhook(name,repourl,secret,jarfile,dagname)
    $("#githubModal").modal('hide');
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  async remove(repo:any){
    await this.service.removeGithubWebhook(repo.key);
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  selectJarFile(){
    this.dags = this.jobs.filter((ele:any)=>{ return ele.jarname == this.jarfile.nativeElement.value})
  }
  selectJarFiler(){
    this.dags = this.jobs.filter((ele:any)=>{ return ele.jarname == this.jarfiler.nativeElement.value})
  }
  
  async createRabbit(){
    let host = this.rabbithost.nativeElement.value
    let port = this.rabbitport.nativeElement.value
    let user = this.rabbituser.nativeElement.value
    let pwd = this.rabbitpwd.nativeElement.value
    await this.service.saveRabbitChannel(host,user,pwd,port)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  async saveQueue(){
    let queue = this.rabbitqueue.nativeElement.value
    let jarFile = this.jarfiler.nativeElement.value
    let dag = this.dagnamer.nativeElement.value
    await this.service.addQueue(queue,jarFile,dag)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  async removeQueue(item:any){
    await this.service.delQueue(item.queue)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
}
