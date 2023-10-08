import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';

@Component({
  selector: 'app-rabbit-modal',
  templateUrl: './rabbit-modal.component.html',
  styleUrls: ['./rabbit-modal.component.css']
})
export class RabbitModalComponent {

  @Input("jars") jars!:any[];
  @Input("dags") dags!:any[];
  @Output() selectJar = new EventEmitter<any>();

  @ViewChild("rabbithost") rabbithost!:ElementRef;
  @ViewChild("rabbitport") rabbitport!:ElementRef;
  @ViewChild("rabbituser") rabbituser!:ElementRef;
  @ViewChild("rabbitpwd") rabbitpwd!:ElementRef;
  @ViewChild("rabbitqueue") rabbitqueue!:ElementRef;
  @ViewChild("jarfiler") jarfiler!:ElementRef;
  @ViewChild("dagnamer") dagnamer!:ElementRef;

  rbuser!:any
  rbpwd!:any
  rbhost!:any
  rbport!:any
  queues:any[] = []

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service3: PropsInputPort){
  }

  async ngOnInit() {
    let props = await this.service3.properties()
    let propsrabbit = props.filter((ele:any)=>{ return ele.group == 'RABBIT_PROPS' })
    for (let index = 0; index < propsrabbit.length; index++) {
      const element = propsrabbit[index];
      this.rbuser = (element.name == "username")?element.value:this.rbuser
      this.rbpwd = (element.name == "password")?"******":this.rbpwd
      this.rbhost = (element.name == "host")?element.value:this.rbhost
      this.rbport = (element.name == "port")?element.value:this.rbport
      if(element.value == "rabbit_consumer_queue"){
        let dagname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "dagname" })[0].value
        let jarname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "jarname" })[0].value
        this.queues.push({queue:element.name,dagname:dagname,jarname:jarname})
      }
    }
  }
  async createRabbit(){
    let host = this.rabbithost.nativeElement.value
    let port = this.rabbitport.nativeElement.value
    let user = this.rabbituser.nativeElement.value
    let pwd = this.rabbitpwd.nativeElement.value
    await this.service.saveRabbitChannel(host,user,pwd,port)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  async saveQueue(){
    let queue = this.rabbitqueue.nativeElement.value
    let jarFile = this.jarfiler.nativeElement.value
    let dag = this.dagnamer.nativeElement.value
    await this.service.addQueue(queue,jarFile,dag)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  async removeQueue(item:any){
    await this.service.delQueue(item.queue)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  selectJarFiler(){
    this.selectJar.emit(this.jarfiler.nativeElement.value)
  }
}
