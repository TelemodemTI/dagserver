import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
declare var $:any
@Component({
  selector: 'app-upload-modal',
  templateUrl: './upload-modal.component.html',
  styleUrls: ['./upload-modal.component.css']
})
export class UploadModalComponent {
  
  @ViewChild("inputFileUploadModal") file!:ElementRef;
  @Output() uploadEvent = new EventEmitter<any>();
  @Output() selectEvent = new EventEmitter<any>();
  @Input("id") id:any;
  event_req!:any
  

  show() {
    $("#"+this.id).modal("show")
  }
  close(){
    $("#"+this.id).modal("hide")
  }
  upload(event: any) {
    this.event_req = event
    this.selectEvent.emit(this.event_req)
  }
  async save(){
    let file = this.event_req.target.files[0]
    this.uploadEvent.emit(file)
  }
}
