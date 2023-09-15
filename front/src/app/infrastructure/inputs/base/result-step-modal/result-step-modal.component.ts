import { Component, Input } from '@angular/core';
declare var $:any;
@Component({
  selector: 'app-result-step-modal',
  templateUrl: './result-step-modal.component.html',
  styleUrls: ['./result-step-modal.component.css']
})
export class ResultStepModalComponent {
  
  data!:any

  show(data:any){
    this.data = data
    console.log(data)
    $('#result-step-modal').modal('show');    
  }
}
