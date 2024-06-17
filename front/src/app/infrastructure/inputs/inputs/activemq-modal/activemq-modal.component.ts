import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';

@Component({
  selector: 'app-activemq-modal',
  templateUrl: './activemq-modal.component.html',
  styleUrls: ['./activemq-modal.component.css']
})
export class ActivemqModalComponent {

  @Input("jars") jars!:any[];
  @Input("dags") dags!:any[];
  @Output() selectJar = new EventEmitter<any>();

  @ViewChild("brokerurl") brokerurl!:ElementRef;
  @ViewChild("brokeruser") brokeruser!:ElementRef;
  @ViewChild("brokerpwd") brokerpwd!:ElementRef;
  @ViewChild("amqueue") amqueue!:ElementRef;
  @ViewChild("jarfiler") jarfiler!:ElementRef;
  @ViewChild("dagnamer") dagnamer!:ElementRef;

  amuser!:any
  ampwd!:any
  amhost!:any
  queues:any[] = []
  error_msg!:any
  error_msg2!:any

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service3: PropsInputPort){
  }
  async ngOnInit() {
    let props = await this.service3.properties()
    let propsrabbit = props.filter((ele:any)=>{ return ele.group == 'ACTIVEMQ_PROPS' })
    
    for (let index = 0; index < propsrabbit.length; index++) {
      const element = propsrabbit[index];
      console.log(element)
      this.amuser = (element.name == "user")?element.value:this.amuser
      this.ampwd = (element.name == "pwd")?"******":this.ampwd
      this.amhost = (element.name == "host")?element.value:this.amhost
      
      if(element.value == "activemq_consumer_listener"){
        let dagname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "dagname" })[0].value
        let jarname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "jarname" })[0].value
        this.queues.push({queue:element.name,dagname:dagname,jarname:jarname})
      }

    }
  }

  async createAM(){
    try {
      let host = this.brokerurl.nativeElement.value.trim()
      let user = this.brokeruser.nativeElement.value.trim()
      let pwd = this.brokerpwd.nativeElement.value.trim()
      if(host && user && pwd ){
        this.error_msg = ""
        await this.service.saveActiveMQChannel(host,user,pwd)
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
          this.router.navigateByUrl(`auth/channels`);
        });
      } else {
        this.error_msg = "All values ​​are required."
      }  
    } catch (error) {
      this.error_msg = "All values ​​are required. Port is a number."
    }
  }
  selectJarFiler(){
    this.selectJar.emit(this.jarfiler.nativeElement.value)
  }
  async saveQueue(){
    let queue = this.amqueue.nativeElement.value.trim()
    let jarFile = this.jarfiler.nativeElement.value.trim()
    let dag = this.dagnamer.nativeElement.value.trim()
    let itemarr = this.queues.filter((ele:any)=>{ return ele.queue == queue && ele.dagname == dag && ele.jarname == jarFile})
    if(itemarr.length == 0){
      if(queue && jarFile && dag){
          this.error_msg2 = ""
          await this.service.addQueueAM(queue,jarFile,dag)
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
            this.router.navigateByUrl(`auth/channels`);
          });
        } else {
          this.error_msg2 = "All values ​​are required.";
        }
    } else {
      this.error_msg2 = "Consumer already exists.";
    }
  }
  async removeQueue(item:any){
    await this.service.delQueueAM(item.queue)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
}
