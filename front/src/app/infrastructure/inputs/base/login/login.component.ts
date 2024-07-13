
import { Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { LoginInputPort } from 'src/app/application/inputs/login.input.port';

declare var window:any
declare var $:any
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  @ViewChild("userinput") localinput!:ElementRef
  @ViewChild("passinput") passinput!:ElementRef
  message!:any
  rutaBase!:any
  version!:any

  constructor(private router: Router, 
    private service: LoginInputPort){
  }

  async ngOnInit() {
    $(document).on('keypress',(e:any)=> {
      if(e.which == 13) {
          this.login()
      }
    });
    if(localStorage.getItem("dagserver_token")){
      this.router.navigateByUrl("auth")
    }
    //let base = (window['base-href'].startsWith("/auth/"))?"/":window['base-href']
    let base = "/"
    const segmentos = base.split('/');
    segmentos.pop();
    this.rutaBase = segmentos.join('/');
    this.rutaBase = (this.rutaBase)?this.rutaBase:"/"
    this.rutaBase = this.rutaBase.endsWith("/")?this.rutaBase:this.rutaBase+"/"
    this.version = await this.service.version();
  }
  async login() {
    const user = this.localinput.nativeElement.value;
    const pwd = this.passinput.nativeElement.value;
    let rv = await this.service.login(user, pwd)
    if (rv) {
      this.message = ""
      this.router.navigateByUrl("auth");
    } else {
      this.message = "Error: Incorrect credentials."
      setTimeout(()=>{
        this.message = ""
      },5000)
    }
  }
  async goToDocs(){
    window.location.href = "https://telemodemti.github.io/dagserver/"
  }
}
