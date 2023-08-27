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
  
  @ViewChild("reponame") reponame!:ElementRef;
  @ViewChild("repourl") repourl!:ElementRef;
  @ViewChild("reposecret") reposecret!:ElementRef;
  @ViewChild("jarfile") jarfile!:ElementRef;
  @ViewChild("dagname") dagname!:ElementRef;

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service2: JobsInputPort){
  }

  async ngOnInit() {
    this.items = []
    this.items = await this.service.getChannels()
    this.jobs = await this.service2.getAvailableJobs()
  }
  options(item:any){
    $("#githubModal").modal('show');
    this.propsSelected = item.props
    console.log(this.propsSelected)
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
}
