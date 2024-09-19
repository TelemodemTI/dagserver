import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { KeystoreInputPort } from 'src/app/application/inputs/keystore.input.port';
declare var $:any
@Component({
  selector: 'app-keystore-content',
  templateUrl: './keystore-content.component.html',
  styleUrls: ['./keystore-content.component.css']
})
export class KeystoreContentComponent {
  entries:any[] = []
  error_msg:string = ""
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
}
