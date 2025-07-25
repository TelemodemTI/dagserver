import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExistingJInputPort } from 'src/app/application/inputs/existingj.input.port';
import { LogDetailInputPort } from 'src/app/application/inputs/logdetail.input.port';


@Component({
  selector: 'app-logdetail',
  templateUrl: './logdetail.component.html',
  styleUrls: ['./logdetail.component.css']
})
export class LogdetailComponent {

  constructor(private router: Router, 
    private route: ActivatedRoute, 
    private service: LogDetailInputPort,
    private eservice: ExistingJInputPort){
  }

  logid! : any;
  item!: any;
  dagname:any = "";
  status!:any
  xcom!:any
  exceptions:any[] = []

  async ngOnInit() {
    this.dagname = this.route.snapshot.paramMap.get('dagname');
    this.logid = this.route.snapshot.paramMap.get('logid');
    var result = await this.service.logs(this.dagname)
    this.item = result.filter((el:any)=>{ return el.id == this.logid})[0]
    this.status = JSON.parse(this.item.status)
    this.xcom = JSON.parse(this.item.outputxcom.replace(/\\n/g, "<br />"))
    this.eservice.getExceptionsFromExecution(this.item.evalkey).then((exceptions:any[])=>{
      this.exceptions = exceptions;
    })
  }  
  refresh(){
    this.ngOnInit()
  }
  back(){
    this.router.navigateByUrl(`auth/jobs/${this.dagname}`);
  }
  expDetail(item:any){
    const blob = new Blob([item.stack], { type: 'text' });
    const url= window.URL.createObjectURL(blob);
    window.open(url);
  }
}
