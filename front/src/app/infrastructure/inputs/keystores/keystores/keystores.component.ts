import { Component, OnInit } from '@angular/core';
declare var $:any
@Component({
  selector: 'app-keystores',
  templateUrl: './keystores.component.html',
  styleUrls: ['./keystores.component.css']
})
export class KeystoresComponent implements OnInit  {
  
  ngOnInit(): void {
    setTimeout(()=> {
      var table = $('#dataTables-keystores-entries').DataTable({responsive: true});
      table.on('search.dt',  (e:any, settings:any)=> {
      })
    },100)
  }

}
