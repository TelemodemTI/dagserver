import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
declare var $:any;
@Component({
  selector: 'app-git-hub-modal',
  templateUrl: './git-hub-modal.component.html',
  styleUrls: ['./git-hub-modal.component.css']
})
export class GitHubModalComponent {

  @ViewChild("reponame") reponame!:ElementRef;
  @ViewChild("repourl") repourl!:ElementRef;
  @ViewChild("reposecret") reposecret!:ElementRef;
  @ViewChild("jarfile") jarfile!:ElementRef;
  @ViewChild("dagname") dagname!:ElementRef;

  @Input("jars") jars!:any[];
  @Input("dags") dags!:any[];
  @Input("propsSelected") propsSelected!:any[];
  @Output() selectJar = new EventEmitter<any>();
  
  
  constructor(private router: Router, 
    private service: InputsChannelsInputPort){
      
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
    this.selectJar.emit(this.jarfile.nativeElement.value)
  }
}
