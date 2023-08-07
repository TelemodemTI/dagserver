import { Component, ElementRef, Input, SimpleChanges, ViewChild } from '@angular/core';
declare var $:any
@Component({
  selector: 'app-jardetailp',
  templateUrl: './jardetailp.component.html',
  styleUrls: ['./jardetailp.component.css']
})
export class JardetailpComponent {

  @Input("selectedDag") selectedDag:any
  @Input("selectedStep") selectedStep:any
  @Input("selectedStepParams") selectedStepParams:any

  @ViewChild("loader") loader!:ElementRef;
  @ViewChild("form") form!:ElementRef;

  ngOnChanges(changes: SimpleChanges) {
    console.log(this.selectedStepParams)
    this.loader?.nativeElement.classList.add("invisible");
    this.form?.nativeElement.classList.remove("invisible")
  }

  show(){
    this.loader?.nativeElement.classList.remove("invisible");
    this.form?.nativeElement.classList.add("invisible")
    $('#param-modaljardetailj').modal('show');
  }
}
