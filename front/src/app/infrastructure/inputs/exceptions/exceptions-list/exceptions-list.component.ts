import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ExceptionsInputPort } from 'src/app/application/inputs/exceptions.input.port';
declare var $:any
@Component({
  selector: 'app-exceptions-list',
  templateUrl: './exceptions-list.component.html',
  styleUrls: ['./exceptions-list.component.css']
})
export class ExceptionsListComponent implements OnInit {
  exceptions:any[] = []
  constructor(private service: ExceptionsInputPort,private router: Router){}
  async ngOnInit() {
    this.exceptions = await this.service.getExceptions();
    setTimeout(()=> {
      var table = $('#dataTables-exceptions').DataTable({responsive: true});
      table.on('search.dt',  (e:any, settings:any)=> {
      })
    },100)
    
  }
  expDetail(item:any){
    const blob = new Blob([item.stack], { type: 'text' });
    const url= window.URL.createObjectURL(blob);
    window.open(url);
  }
  async deleteExc(item:any){
    await this.service.removeException(item.eventDt)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"exceptions"]);
    }); 
  }
  
}
