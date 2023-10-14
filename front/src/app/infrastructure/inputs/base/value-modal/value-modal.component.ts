import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
declare var $:any;
@Component({
  selector: 'app-value-modal',
  templateUrl: './value-modal.component.html',
  styleUrls: ['./value-modal.component.css']
})
export class ValueModalComponent {
  @ViewChild("inputValueModal") inputValueModal!:ElementRef;
  @Input("param") param:any;
  @Input("actualValue") actualValue:any;
  @Output() changeValueEvent = new EventEmitter<any>();
  error_msg!:any
  saveValue(){
    this.error_msg = ""
    let value = this.inputValueModal.nativeElement.value
    if(value){
      this.changeValueEvent.emit([this.param,value])
    } else {
      this.error_msg = "All values ​​are required."
    }
    
  }
  show(){
    $('#value-inputer').modal('show');    
  }
}
