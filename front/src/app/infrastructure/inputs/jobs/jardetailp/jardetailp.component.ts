import { Component, ElementRef, Input, SimpleChanges, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { JardetailpInputPort } from 'src/app/application/inputs/jardetailp.input.port';
declare var $:any
declare var CodeMirror:any
@Component({
  selector: 'app-jardetailp',
  templateUrl: './jardetailp.component.html',
  styleUrls: ['./jardetailp.component.css']
})
export class JardetailpComponent {

  @Input("selectedDag") selectedDag:any
  @Input("selectedStep") selectedStep:any
  @Input("selectedStepParams") selectedStepParams:any
  @Input("selectedStepOpts") selectedStepOpts:any
  @Input("selectedStepMetadata") selectedStepMetadata:any
  @Input("jarname") jarname:any
  

  @ViewChild("loader") loader!:ElementRef;
  @ViewChild("form") form!:ElementRef;

  editor!:any

  constructor(private router: Router,private service: JardetailpInputPort){
  }
  

  ngOnChanges(changes: SimpleChanges) {
    this.initCodemirror().then((flag)=>{
      this.loader?.nativeElement.classList.add("invisible");
      this.form?.nativeElement.classList.remove("invisible")
      if(this.editor){
        let param = this.selectedStepMetadata.params.filter((ele:any)=>{ return ele.type == "sourcecode" })[0]
        this.editor.setValue(this.selectedStepParams[param.name]) 
      }
    })
    
  }
  close(){
    $('#param-modaljardetailj').modal('hide');
  }
  show(){
    this.loader?.nativeElement.classList.remove("invisible");
    this.form?.nativeElement.classList.add("invisible")
    $('#param-modaljardetailj').modal('show');
  }
  refreshCodemirror(){
    let interval = setInterval(()=>{
      if(this.loader?.nativeElement.classList.contains("invisible")){
        clearInterval(interval)
        setTimeout(() => {
          this.editor.refresh()
        }, 300);
      } 
    },100)
  }
  async updateParams(){

    let paramarr = []
    for (let index = 0; index < this.selectedStepMetadata.params.length; index++) {
      const key = this.selectedStepMetadata.params[index];
      if(key.type != "sourcecode"){
        let vlue = $("#param-"+key.name+"-value").val()
        paramarr.push({key:key.name,value:vlue,type:key.type})
      } else {
        let vlue:string = this.editor.getValue()
        paramarr.push({key:key.name,value:vlue,type:key.type})
      }
    }
    console.log(this.selectedStepMetadata)
    for (let index = 0; index < this.selectedStepMetadata.opt.length; index++) {
      const key = this.selectedStepMetadata.opt[index];
      if(key.type != "sourcecode"){
        let vlue = $("#param-"+key.name+"-value").val()
        paramarr.push({key:key.name,value:vlue,type:key.type})
      } else {
        let vlue:string = this.editor.getValue()
        paramarr.push({key:key.name,value:vlue,type:key.type})
      }
    }
    console.log(paramarr)
    let bin = btoa(JSON.stringify(paramarr))
    await this.service.updateParamsCompiled(this.jarname,this.selectedStep,this.selectedStepMetadata.class,bin)
    this.close()
    this.router.navigateByUrl("auth/jobs");
    
  }
  initCodemirror(){
    return new Promise((resolve,reject)=>{
      var width = $("#queryTextqv").attr("width");
      var height = $("#queryTextqv").attr("height");
      var read = $("#queryTextqv").data("readonly"); 
      var lineWrapping = (read)?true:false;
      setTimeout(()=>{
        try {
          var obj = document.getElementById("queryTextqv")
          this.editor = CodeMirror.fromTextArea(obj, {
                lineNumbers: true,
                lineWrapping: lineWrapping,
                readOnly: read,
                matchBrackets: true,
                mode: "simplemode",
                continueComments: "Enter"
          })
          this.editor.setSize(width,height)  
          this.editor.refresh();  
        } catch (error) {
          console.log("error en codemirror loading")
        }
        
        resolve(true)
      },1)
    })
  }
}
