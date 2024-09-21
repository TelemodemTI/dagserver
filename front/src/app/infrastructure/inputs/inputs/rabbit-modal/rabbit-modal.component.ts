import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { KeystoreInputPort } from 'src/app/application/inputs/keystore.input.port';
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
  @ViewChild("rabbitcred") rabbitcred!:ElementRef;
  @ViewChild("rabbitqueue") rabbitqueue!:ElementRef;
  @ViewChild("jarfiler") jarfiler!:ElementRef;
  @ViewChild("dagnamer") dagnamer!:ElementRef;

  entries:any[] = []
  rbcred!:any
  rbhost!:any
  rbport!:any
  queues:any[] = []
  error_msg!:any
  error_msg2!:any

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service3: PropsInputPort,
    private keystore: KeystoreInputPort){
  }

  async ngOnInit() {
    this.entries = await this.keystore.getEntries();
    let props = await this.service3.properties()
    let propsrabbit = props.filter((ele:any)=>{ return ele.group == 'RABBIT_PROPS' })
    for (let index = 0; index < propsrabbit.length; index++) {
      const element = propsrabbit[index];
      this.rbcred = (element.name == "cred")?element.value:this.rbcred
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
    try {
      let host = this.rabbithost.nativeElement.value.trim()
      let port = parseInt(this.rabbitport.nativeElement.value.trim())
      let cred = this.rabbitcred.nativeElement.value.trim()
      
      if(host && port && cred ){
        this.error_msg = ""
        await this.service.saveRabbitChannel(host,cred,port)
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
  async saveQueue(){
    let queue = this.rabbitqueue.nativeElement.value.trim()
    let jarFile = this.jarfiler.nativeElement.value.trim()
    let dag = this.dagnamer.nativeElement.value.trim()
    let itemarr = this.queues.filter((ele:any)=>{ return ele.queue == queue && ele.dagname == dag && ele.jarname == jarFile})
    if(itemarr.length == 0){
		if(queue && jarFile && dag){
	      this.error_msg2 = ""
	      await this.service.addQueue(queue,jarFile,dag)
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
    await this.service.delQueue(item.queue)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  selectJarFiler(){
    this.selectJar.emit(this.jarfiler.nativeElement.value)
  }
}
