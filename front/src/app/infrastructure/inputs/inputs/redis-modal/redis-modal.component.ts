import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';

@Component({
  selector: 'app-redis-modal',
  templateUrl: './redis-modal.component.html',
  styleUrls: ['./redis-modal.component.css']
})
export class RedisModalComponent {

  @Input("jars") jars!:any[];
  @Input("dags") dags!:any[];
  @Output() selectJar = new EventEmitter<any>();

  @ViewChild("jarfiled") jarfiled!:ElementRef;
  @ViewChild("dagnamed") dagnamed!:ElementRef;
  @ViewChild("redismode") redismode!:ElementRef;
  @ViewChild("redishost") redishost!:ElementRef;
  @ViewChild("redisport") redisport!:ElementRef;
  @ViewChild("redischannel") redischannel!:ElementRef;

  error_msg!:any
  error_msg2!:any
  redmode!:any
  redhost!:any
  redport!:any
  channels:any[] = []
  

  constructor(private router: Router, 
    private service: InputsChannelsInputPort,
    private service3: PropsInputPort
    ){
  }

  async ngOnInit() {
    let props = await this.service3.properties()
    let redisprops = props.filter((ele:any)=>{ return ele.group == 'REDIS_PROPS' })
    console.log(redisprops)
    for (let index = 0; index < redisprops.length; index++) {
      const element = redisprops[index];
      this.redmode = (element.name == "mode")?element.value:this.redmode
      this.redhost = (element.name == "hostname")?element.value:this.redhost
      this.redport = (element.name == "port")?element.value:this.redport
      if(element.value == "redis_consumer_listener"){
        let dagname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "dagname" })[0].value
        let jarname = props.filter((ele:any)=>{ return ele.group == element.name && ele.name == "jarname" })[0].value
        this.channels.push({channel:element.name,dagname:dagname,jarname:jarname})
      }
    }
  }

  async createRedis(){
    let mode = this.redismode.nativeElement.value.trim()
    let host = this.redishost.nativeElement.value.trim()
    let port = this.redisport.nativeElement.value.trim()
    
    if(mode && host && port){
      this.error_msg = "";
      await this.service.saveRedisChannel(mode,host,port)
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigateByUrl(`auth/channels`);
      });
    } else {
      this.error_msg = "All values ​​are required.";
    }
  }
  async saveListener(){
    let channel = this.redischannel.nativeElement.value.trim()
    let jarFile = this.jarfiled.nativeElement.value.trim()
    let dag = this.dagnamed.nativeElement.value.trim()
    if(channel && jarFile && dag){
      this.error_msg2 = ""
      await this.service.addListener(channel,jarFile,dag)
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigateByUrl(`auth/channels`);
      });
    } else {
      this.error_msg2 = "All values ​​are required.";
    }
    
  }
  async removeListener(item:any){
    await this.service.delListener(item.channel)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
  selectJarFiled(){
    this.selectJar.emit(this.jarfiled.nativeElement.value);
  }
}
