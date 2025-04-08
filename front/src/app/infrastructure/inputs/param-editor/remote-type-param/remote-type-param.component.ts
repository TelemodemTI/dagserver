import { Component, ElementRef, Input, ViewChild } from '@angular/core';
declare var $:any
@Component({
  selector: 'app-remote-type-param',
  templateUrl: './remote-type-param.component.html',
  styleUrls: ['./remote-type-param.component.css']
})
export class RemoteTypeParamComponent  {
  
  @ViewChild("remoterActionSelector") remoterActionSelector!:ElementRef;
  @ViewChild("remoterInput1") remoterInput1!:ElementRef;
  @ViewChild("remoterInput2") remoterInput2!:ElementRef;
  @Input("generatedIdParams") generatedIdParams:any
  remote_cmd:string[] = []


  setValue(value:any){
    this.remote_cmd = value.split(";")
  }
  getValue(){
    return this.remote_cmd.join(";")
  }
  remove(i:number){
    this.remote_cmd.splice(i,1)
  }
  getRemoteCmdValue(i:number,subcat:number){
    let varb = this.remote_cmd[i].split(" ")
    if(varb[subcat]){
      return varb[subcat]
    } else return ''
    
  }
  remoteAdd(){
    var action = $("#remoter-action-selector").val()
    var filepath = this.remoterInput1.nativeElement.value + " " + this.remoterInput2.nativeElement.value
    this.remote_cmd.push(action+" "+filepath)
  }
  onChange(){
    let value = this.remoterActionSelector.nativeElement.value
    if(value=="list"){
      this.remoterInput2.nativeElement.disabled = 'true'
    } else {
      this.remoterInput2.nativeElement.disabled = ''
    }
  }
  
}
