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
  constructor(private service: KeystoreInputPort,private router: Router){}
  async ngOnInit() {
    this.entries = await this.service.getEntries();
    setTimeout(()=> {
      var table = $('#dataTables-keystore').DataTable({responsive: true});
      table.on('search.dt',  (e:any, settings:any)=> {
      })
    },100) 
  }
}
