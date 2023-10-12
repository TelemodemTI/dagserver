import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogsInputPort } from 'src/app/application/inputs/logs.input.port';

declare var $:any;
@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.css']
})
export class LogsComponent {


  constructor(private route: ActivatedRoute, 
    private router: Router,
    private service: LogsInputPort){
  }

  dagname:any = "";
  logs :any[] = []
  last !: any
  sort:any = "fa-angle-double-down";
  table!:any
  async ngOnInit() {
    this.dagname = this.route.snapshot.paramMap.get('dagname');
    this.logs = await this.service.logs(this.dagname)
    console.log(this.logs)
    this.logs.sort((a:number,b:number)=> a < b ? 1 : -1);
    if(this.logs.length > 0){
      this.last = this.logs.reduce((a, b) => (a.execDt > b.execDt ? a : b));
    }
    setTimeout(()=>{
      if(!this.table){
        this.table = $('#dataTables-logs').DataTable({
              responsive: true,
              columnDefs: [
                { "width": "20%", "targets": [0,1] }
              ]
        });
      }
    },100)      
  }
  async refresh(){
    this.logs = await this.service.logs(this.dagname)
    this.logs.sort((a:number,b:number)=> a < b ? 1 : -1);
    if(this.logs.length > 0){
      this.last = this.logs.reduce((a, b) => (a.execDt > b.execDt ? a : b));
    }
  }
  sorter(){
    this.sort = this.sort == 'fa-angle-double-up' ? "fa-angle-double-down" : "fa-angle-double-up"
    if(this.sort == "fa-angle-double-down"){
      this.logs.sort((a:number,b:number)=> a > b ? 1 : -1);
    } else {
      this.logs.sort((a:number,b:number)=> a < b ? 1 : -1);
    }
  }
  logDEtail(item:any){
    this.router.navigateByUrl(`auth/jobs/${item.dagname}/${item.id}`);
  }
  back(){
    this.router.navigateByUrl("auth/jobs");
  }
  async remove(item:any){
    await this.service.removeLog(item.id);
    await this.router.navigateByUrl('/', { skipLocationChange: true })
    this.router.navigateByUrl(`auth/jobs/${this.dagname}`);
  }
  async removeAll(){
    await this.service.removeAllLog(this.dagname);
    await this.router.navigateByUrl('/', { skipLocationChange: true })
    this.router.navigateByUrl(`auth/jobs/${this.dagname}`);
  }
}
