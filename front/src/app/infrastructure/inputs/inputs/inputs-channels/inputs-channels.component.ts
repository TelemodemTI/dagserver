import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
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
    private service2: JobsInputPort){
  }

  async ngOnInit() {
    this.items = []
    this.items = await this.service.getChannels()
    this.jobs = await this.service2.getAvailableJobs()
    let jarsf = this.jobs.map((eleme:any)=>{return eleme.jarname })
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
    console.log(this.dags)
  }
  selectJarFiler(){
    this.dags = this.jobs.filter((ele:any)=>{ return ele.jarname == this.jarfiler.nativeElement.value})
    console.log(this.dags)
  }
  
  createRabbit(){

  }
}
