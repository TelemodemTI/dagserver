import { Component, EventEmitter, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
import { AvailableJobs } from 'src/app/domain/models/availableJobs.model';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';
declare var $:any;
@Component({
  selector: 'app-compiled-tab',
  templateUrl: './compiled-tab.component.html',
  styleUrls: ['./compiled-tab.component.css']
})
export class CompiledTabComponent {
  @ViewChild("valuer") valuer!:ValueModalComponent
  scheduled:any = []
  jobs:AvailableJobs[] = []
  title_msje:any = "Error"
  error_msje:any = ""
  table!:any

  jarname!:string
  dagname!:string
  constructor(private router: Router, 
    private service: JobsInputPort){
  }

  async ngOnInit(): Promise<void> {
    this.jobs = await this.service.getAvailableJobs()
    this.calculateActive()
    setTimeout(()=>{
      if(!this.table){
        this.table = $('#dataTables-jobs').DataTable({ responsive: true });
      }
    },500)
  }

  async calculateActive(){
    this.scheduled = await this.service.getScheduledJobs()
    if(this.jobs && this.scheduled){
        for (let job of this.jobs) {
          let scheduledJob = this.scheduled.find((scheduled:any) => scheduled.dagname === job.dagname);
          if (scheduledJob) {
            job.triggerEventTarget = scheduledJob.eventTrigger;
            job.nextFireAt = scheduledJob.nextFireAt;
            job.hasScheduled = true; 
          } else {
            job.hasScheduled = false; 
          }
        }
    }
  }

  async hasScheduled(item:any){
    if(item.jarname != "SYSTEM"){
      var token = localStorage.getItem("dagserver_token")
      var string = ""
      var name = ""
      if(item.hasScheduled){
        this.unschedule(item);
      } else {
        this.schedule(item);
      }
    }
  }
  async unschedule(item:any){
    try {
      await this.service.unscheduleDag(item.dagname,item.jarname)
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"jobs"]);
      });   
    } catch (error) {
      this.error_msje = error
      $('#propertyNotFoundModal').modal('show');
    }
  }
  async schedule(item:any){
    try {
      await this.service.scheduleDag(item.dagname,item.jarname)  
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"jobs"]);
      });   
    } catch (error) {
      this.error_msje = error
      console.log(error)
      $('#propertyNotFoundModal').modal('show');
    }
  }
  async changeValueEvent(data:any){
    this.valuer.close();
    try {
      this.playReal(this.jarname,this.dagname,data[1])  
    } catch (error) {
      this.title_msje = "error"
      this.error_msje = error
      $('#propertyNotFoundModal').modal('show');  
    }
  }
  async play(jarname:any,dagname:any){
    this.dagname = dagname
    this.jarname = jarname
    this.valuer.show()
  }
  async playReal(jarname:any,dagname:any,data:string){
    let msg = await this.service.executeDag(dagname,jarname,data);
    this.title_msje = msg.title_msje
    this.error_msje = msg.error_msje
    $('#propertyNotFoundModal').modal('show');
  }
  jobDetail(jarname:any,dagname:any){
    this.router.navigateByUrl(`auth/jobs/jarname/${jarname}/${dagname}`);
  }
  viewLogs(dagname:any){
    this.router.navigateByUrl(`auth/jobs/${dagname}`);
  }
  dependencies(jarname:any,dagname:any){
    this.router.navigateByUrl(`auth/dependencies/${jarname}/${dagname}`);
  }
  refresh(){
    this.scheduled = []
    this.jobs = []
    this.title_msje = "Error"
    this.error_msje = ""
    this.ngOnInit();
  }
  async remove(jarname:any){
    await this.service.remove(jarname);
    setTimeout(()=>{
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"jobs"]);
      });   
    },2000)
    
  }
  async importJarDesign(jarname:any){
    let result = await this.service.reimport(jarname)  
    if(result.code == 200){
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"jobs"]);
      });   
      alert("dag imported successfully")
    } else {
      alert(result.value)
    }
  }
}
