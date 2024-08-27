import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';
declare var $:any;
@Component({
  selector: 'app-http-api-modal',
  templateUrl: './http-api-modal.component.html',
  styleUrls: ['./http-api-modal.component.css']
})
export class HttpApiModalComponent {
  error_msg:string = "";
  @Input("propsSelected") propsSelected!:any[];
  @ViewChild("appname") appname!:ElementRef;
  constructor(private service: InputsChannelsInputPort, private router: Router){ }
  async createHttpApiKey(){
    let value = this.appname.nativeElement.value
    await this.service.createApiKey(value)
    $("#httpModal").modal('hide');
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigateByUrl(`auth/channels`);
    });
  }
  async remove(option:any){
    await this.service.deleteApiKey(option.key)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(`auth/channels`);
    });
  }
}
