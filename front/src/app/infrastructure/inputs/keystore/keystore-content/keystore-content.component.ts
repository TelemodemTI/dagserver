import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { KeystoreInputPort } from 'src/app/application/inputs/keystore.input.port';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';
declare var $:any
@Component({
  selector: 'app-keystore-content',
  templateUrl: './keystore-content.component.html',
  styleUrls: ['./keystore-content.component.css']
})
export class KeystoreContentComponent {
  entries:any[] = []
  error_msg:string = ""
  @ViewChild("valuer") valuer!:ValueModalComponent
  constructor(private service: KeystoreInputPort,private router: Router){}
  async ngOnInit() {
    this.entries = await this.service.getEntries();
    console.log(this.entries)
    setTimeout(()=> {
      var table = $('#dataTables-keystore').DataTable({responsive: true});
      table.on('search.dt',  (e:any, settings:any)=> {
      })
    },100) 
  }
  async savenewEntry(){
    var name = $("#namepropkeyinput").val().trim()
    var user = $("#usernamepropkeyinput").val().trim()
    var pwd = $("#pwdpropkeyinput").val().trim()
    await this.service.createEntry(name,user,pwd)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"keystore"]);
    });
  }
  async removeEntry(name:any){
    await this.service.removeEntry(name);
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"keystore"]);
    });
  }
  async downloadKeystore(){
    this.valuer.show();
  }
  async downloadKeystoreEvent(value:any){
    this.valuer.close();
    let password = value[1]
    this.service.downloadKeystore(password);
  }
}
