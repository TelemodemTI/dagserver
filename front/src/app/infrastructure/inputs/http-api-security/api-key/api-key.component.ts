import { ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';
@Component({
  selector: 'app-api-key',
  templateUrl: './api-key.component.html',
  styleUrls: ['./api-key.component.css']
})
export class ApiKeyComponent implements OnInit {
  error_msg:string = "";
  @ViewChild("appname") appname!:ElementRef;
  
  constructor(private service: InputsChannelsInputPort, private propservice: PropsInputPort,private router: Router,private cdr: ChangeDetectorRef){ }
  
  propsApis:any[] = [];

  async ngOnInit() {
    this.propsApis = [];
    let properties1 = await this.propservice.properties()
    this.propsApis = properties1.filter((elem:any)=>{ return elem.group == "HTTP_CHANNEL_API_KEY"})
    console.log(this.propsApis)
    this.cdr.detectChanges();
  }
  async createHttpApiKey(){
    let value = prompt("Enter the application name")
    await this.service.createApiKey(value)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/api-keys`);
    });
  }
  async remove(option:any){
    await this.service.deleteApiKey(option.name)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/api-keys`);
    });
  }
}
