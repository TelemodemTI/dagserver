import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { DagPropsInputPort } from 'src/app/application/inputs/dagprops.input.port';
import { AvailableJobs } from 'src/app/domain/models/availableJobs.model';
declare var $:any

@Component({
  selector: 'app-dag-props',
  templateUrl: './dag-props.component.html',
  styleUrls: ['./dag-props.component.css']
})
export class DagPropsComponent {
    @Input('dagName') dagName!: string
	@Input('group') group!: string
    @Input('data') data!: any
    @Output() saveDagEvent = new EventEmitter<any>();
    @Output() changeDagNameEvent = new EventEmitter<any>();

    typeTrigger:string="cron"
    availables: AvailableJobs[] = []
    groups:any[] = []
    triggerval:any = "0 0/1 * * * ?"
    loc:string = ""
    targetType:string="DAG"
    targetGroup:string=""
    constructor(private service: DagPropsInputPort){
    }

    ngOnChanges(changes: SimpleChanges) {
      setTimeout(()=>{
        $("#props-collapser").trigger("click");
      },50)
      let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.dagName;})[0]
      console.log("triggerval")
        console.log(obj)
        console.log("fin triggerval")
      if(obj.trigger){
        this.changeTrigger(obj.trigger)
        this.triggerval = obj.cron
        this.targetGroup = obj.targetGroup
        this.loc = obj.loc
        this.targetType = obj.target
      }
    }

    saveDag(dagname:string){
      let obj = this.data.dags.filter(( obj:any )=> {return obj.name == dagname;})[0]
      obj.className = "generated_dag.main."+dagname
      obj.group = $("#daggroupinput-"+dagname).val()
      obj.trigger = this.typeTrigger
      obj.target = this.targetType
      if(this.typeTrigger == 'cron'){
        obj.cron = $("#dagcroninput-"+dagname).val()
        obj.loc = "";
        obj.targetDag = "";
        obj.targetGroup = "";
      } else if(this.typeTrigger == 'listener'){
        obj.cron = ""
        obj.loc = this.loc
        obj.targetDag = $("#dagtargetinput-"+dagname).val()
        obj.targetGroup = $("#dagtargetgroupinput-"+dagname).val()
      } else {
        obj.cron = ""
        obj.loc = ""
        obj.targetDag = "";
        obj.targetGroup = ""
      }
      this.saveDagEvent.emit(obj)
    }
    locCheck(loc:string){
      this.loc = loc
    }
    targetCheck(target:string){
      this.targetType = target
    }
    async changeTrigger(type:string){
      if(type == "listener"){
        this.availables = await this.service.getAvailableJobs()
        this.groups = this.availables.map((ele:AvailableJobs)=>{ return ele.groupname}).filter((elem, index, self)=> { return index === self.indexOf(elem)})
      } else {
        this.availables = []
      }
      this.typeTrigger = type
    }

    changeDagName(dagname:any){
      let newname = $("#dagnameinput-"+dagname).val()
      if(newname){
        let obj = this.data.dags.filter(( obj:any )=> {return obj.name == dagname;})[0]
        obj.name = newname
        this.changeDagNameEvent.emit(obj)
      }
    }
    collapse(dagname:any){
      
      let flag = ($("#props-collapser-"+dagname).attr("aria-expanded").toLowerCase() === 'true')?true:false;
      let flags = ($("#props-collapser-son-"+dagname).attr("aria-expanded").toLowerCase() === 'true')?true:false;

      if(flags && !flag){
  
        setTimeout(()=>{
          $("#props-collapser-son").trigger("click");
        },50)
  
      }
    }
}
