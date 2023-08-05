
import { Component, ElementRef, Inject, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { LoginInputPort } from 'src/app/application/inputs/login.input.port';



@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  @ViewChild("userinput") localinput!:ElementRef
  @ViewChild("passinput") passinput!:ElementRef

  constructor(private router: Router, 
    private service: LoginInputPort){
  }

  ngOnInit(): void {
    if(localStorage.getItem("dagserver_token")){
      this.router.navigateByUrl("auth")
    }
  }
  async login() {
    const user = this.localinput.nativeElement.value;
    const pwd = this.passinput.nativeElement.value;
    let rv = await this.service.login(user, pwd)
    console.log(rv)
    if (rv) {

      this.router.navigateByUrl("auth");
    }
  }
  
}
