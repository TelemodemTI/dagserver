import { Component, Input } from '@angular/core';
import { ExistingJInputPort } from 'src/app/application/inputs/existingj.input.port';
declare var $:any;
@Component({
  selector: 'app-result-step-modal',
  templateUrl: './result-step-modal.component.html',
  styleUrls: ['./result-step-modal.component.css']
})
export class ResultStepModalComponent {

  constructor(private service: ExistingJInputPort){}
  data!:any
  exceptions:any[] = []

  show(data:any){
    this.data = data
    this.service.getExceptionsFromExecution(data.evalkey).then((exceptions:any[])=>{
      this.exceptions = exceptions;
      console.log(this.exceptions);
      $('#result-step-modal').modal('show');    
    })
  }


  expDetail(item:any){
    const blob = new Blob([item.stack], { type: 'text' });
    const url= window.URL.createObjectURL(blob);
    window.open(url);
  }
}
