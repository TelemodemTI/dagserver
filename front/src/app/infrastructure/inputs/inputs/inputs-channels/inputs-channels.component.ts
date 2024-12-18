import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
import { AvailableJobs } from 'src/app/domain/models/availableJobs.model';
declare var $:any;
@Component({
  selector: 'app-inputs-channels',
  templateUrl: './inputs-channels.component.html',
  styleUrls: ['./inputs-channels.component.css']
})
export class InputsChannelsComponent {

  items:any[] = []
  propsSelected:any[] =[]
  jobs:AvailableJobs[] = []
  jars:any[] = []
  dags:any[] = []
  
  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service2: JobsInputPort){
  }

  async ngOnInit() {
    this.items = []
    this.items = await this.service.getChannels()
    this.jobs = await this.service2.getAvailableJobs()
    let jarsf = this.jobs.map((eleme:any)=>{return eleme.jarname })
    this.jars = [...new Set(jarsf)];
  }
  options(item:any){
    if(item.name == "RABBIT_PROPS") {
      $("#rabbitModal").modal('show');
    } else if(item.name == "REDIS_PROPS") {
      $("#redisModal").modal('show');
    } else if(item.name == "KAFKA_CONSUMER")  {
      $("#kafkaModal").modal('show');
    } else if(item.name == "HTTP_ENDPOINT")  {
      $("#httpModal").modal('show');
    } else {
	  $("#activeMQModal").modal('show');
	}
    this.propsSelected = item.props
  }

  selectJarFile(file:any){
    this.dags = this.jobs.filter((ele:any)=>{ return ele.jarname == file})
  }
  
}
