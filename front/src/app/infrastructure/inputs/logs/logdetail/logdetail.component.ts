import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogDetailInputPort } from 'src/app/application/inputs/logdetail.input.port';


@Component({
  selector: 'app-logdetail',
  templateUrl: './logdetail.component.html',
  styleUrls: ['./logdetail.component.css']
})
export class LogdetailComponent {

  constructor(private route: ActivatedRoute, 
    private service: LogDetailInputPort){
  }

  logid! : any;
  item!: any;
  dagname:any = "";
  status!:any
  xcom!:any

  async ngOnInit() {
    this.dagname = this.route.snapshot.paramMap.get('dagname');
    this.logid = this.route.snapshot.paramMap.get('logid');
    var result = await this.service.logs(this.dagname)
    this.item = result.filter((el:any)=>{ return el.id == this.logid})[0]
    this.status = JSON.parse(this.item.status)
    this.xcom = JSON.parse(this.item.xcomoutput.replace(/\\n/g, "<br />"))
  }  
}
