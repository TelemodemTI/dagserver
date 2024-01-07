import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';

@Component({
  selector: 'app-kafka-modal',
  templateUrl: './kafka-modal.component.html',
  styleUrls: ['./kafka-modal.component.css']
})
export class KafkaModalComponent {
  @Input("jars") jars!:any[];
  @Input("dags") dags!:any[];
  @Output() selectJar = new EventEmitter<any>();
  @ViewChild("jarfiled") jarfiled!:ElementRef;
  @ViewChild("dagnamed") dagnamed!:ElementRef;
  @ViewChild("kafkabootstrapServers") kafkabootstrapServers!:ElementRef;
  @ViewChild("kafkagroupId") kafkagroupId!:ElementRef;
  @ViewChild("kafkapoll") kafkapoll!:ElementRef;
  @ViewChild("kafkatopic") kafkatopic!:ElementRef;
  
  error_msg!:any
  error_msg2!:any
  bootstrapServers!:any
  groupId!:any
  poll!:any
  channels:any[] = []

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service3: PropsInputPort
    ){
  }
  
  async ngOnInit() {
    let props = await this.service3.properties()
    let redisprops = props.filter((ele:any)=>{ return ele.group == 'KAFKA_PROPS' })
    console.log(redisprops)
    for (let index = 0; index < redisprops.length; index++) {
      const element = redisprops[index];
      this.bootstrapServers = (element.name == "bootstrapServers")?element.value:this.bootstrapServers
      this.groupId = (element.name == "groupId")?element.value:this.groupId
      this.poll = (element.name == "poll")?element.value:this.poll
      if(element.value == "kafka_consumer_listener"){
        let dagname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "dagname" })[0].value
        let jarname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "jarname" })[0].value
        this.channels.push({topic:element.name,dagname:dagname,jarname:jarname})
      }
    }
  }

  async createKafka(){
    let bootstrapServers = this.kafkabootstrapServers.nativeElement.value.trim()
    let groupId = this.kafkagroupId.nativeElement.value.trim()
    let poll = this.kafkapoll.nativeElement.value.trim()
    
    if(bootstrapServers && groupId && poll){
      this.error_msg = "";
      await this.service.saveKafkaChannel(bootstrapServers,groupId,poll)
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigateByUrl(`auth/channels`);
      });
    } else {
      this.error_msg = "All values ​​are required.";
    }
  }

  async saveConsumer(){
    let topic = this.kafkatopic.nativeElement.value.trim()
    let jarFile = this.jarfiled.nativeElement.value.trim()
    let dag = this.dagnamed.nativeElement.value.trim()
    
    let itemarr = this.channels.filter((ele:any)=>{ return ele.topic == topic && ele.dagname == dag && ele.jarname == jarFile})
    if(itemarr.length == 0){
		if(topic && jarFile && dag){
	      this.error_msg2 = ""
	      await this.service.addConsumer(topic,jarFile,dag)
	      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
	        this.router.navigateByUrl(`auth/channels`);
	      });
	    } else {
	      this.error_msg2 = "All values ​​are required.";
	    }	
	} else {
		this.error_msg2 = "Listener already exists.";
	}
  }
  async removeConsumer(item:any){
    await this.service.delConsumer(item.channel)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  selectJarFiled(){
    this.selectJar.emit(this.jarfiled.nativeElement.value);
  }
}
