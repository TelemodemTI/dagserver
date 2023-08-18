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
  saveValue(){
    this.changeValueEvent.emit([this.param,this.inputValueModal.nativeElement.value])
  }
  show(){
    $('#value-inputer').modal('show');    
  }
}
