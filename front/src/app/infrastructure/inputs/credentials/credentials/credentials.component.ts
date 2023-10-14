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
      var userac = $("#usernamepropinput").val().trim();
      var typeac = $("#accountTypeCombo").val().trim();
      var pwd = $("#pwdpropinput").val().trim();
      var repwd = $("#repwdpropinput").val().trim();
      
      if(pwd == repwd){
        if(userac && typeac && pwd && repwd){
          try {
            await this.service.createAccount(userac,typeac,pwd);
            $('#addUserModal').modal('hide');
            this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
              this.router.navigate(['admin',"credentials"]);
            });   
          } catch (error) {
            this.message = error
          }
        } else {
          this.message = "All values ​​are required."
        }
      } else {
        this.message = "Password are not the same"
      }     
  }
  async delete(item:any){
    await this.service.deleteAccount(item.username);
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['admin',"credentials"]);
    }); 
  }
}
