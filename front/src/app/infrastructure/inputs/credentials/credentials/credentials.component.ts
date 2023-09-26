import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticatedInputPort } from 'src/app/application/inputs/authenticated.input.port';
import { CredentialsInputPort } from 'src/app/application/inputs/credentials.input.port';

declare var $:any;
@Component({
  selector: 'app-credentials',
  templateUrl: './credentials.component.html',
  styleUrls: ['./credentials.component.css']
})
export class CredentialsComponent {

  credentials: any[] = []
  localtype:any = ""
  message!:any

  constructor(private router: Router, private service: CredentialsInputPort, private serviceL: AuthenticatedInputPort){}

  async ngOnInit() {
    var res = this.serviceL.getDecodedAccessToken()
    this.localtype = res.typeAccount
    this.credentials = await this.service.getCredentials();
    setTimeout(function () {
      $('#dataTables-credentials').DataTable({responsive: true});
    },100)
  }
  async saveUser(){
      var userac = $("#usernamepropinput").val();
      var typeac = $("#accountTypeCombo").val();
      var pwd = $("#pwdpropinput").val();
      var repwd = $("#repwdpropinput").val();
      
      if(pwd == repwd){
        try {
          await this.service.createAccount(userac,typeac,pwd);
          $('#addUserModal').modal('hide');
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
            this.router.navigate(['admin',"credentials"]);
          });   
        } catch (error) {
          this.message = error
        }
      }      
  }
  async delete(item:any){
    await this.service.deleteAccount(item.username);
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['admin',"credentials"]);
    }); 
  }
}
