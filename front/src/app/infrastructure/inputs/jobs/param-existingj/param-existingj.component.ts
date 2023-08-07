import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
declare var $:any
declare var CodeMirror:any
@Component({
  selector: 'app-param-existingj',
  templateUrl: './param-existingj.component.html',
  styleUrls: ['./param-existingj.component.css']
})
export class ParamExistingjComponent {



  @ViewChild("loader") loader!:ElementRef;
  @ViewChild("form") form!:ElementRef;


  @Input("data") data:any
  @Input("selectedTab") selectedTab:any
  @Input("generatedIdParams") generatedIdParams:any
  @Input("selectedStep") selectedStep:any
  @Input("selectedStepParams") selectedStepParams:any
  @Output() removeStepEvent = new EventEmitter<any>();

  editor!:any

  ngOnChanges(changes: SimpleChanges) {
    this.initCodemirror().then((flag)=>{
      console.log("onchanges") 
      this.loader?.nativeElement.classList.add("invisible");
      this.form?.nativeElement.classList.remove("invisible")
      if(this.editor && this.data){  
        let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.selectedTab;})[0]
        let step = obj.boxes.filter((item:any)=>{ return item.id == this.selectedStep})[0]
        let value = step.params.filter((ele:any)=>{ return ele.type == "sourcecode" })[0]
        this.editor.setValue(value.value) 
      }
    })

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
  show(){
      this.loader?.nativeElement.classList.remove("invisible");
      this.form?.nativeElement.classList.add("invisible")
      if(!this.editor){
        this.initCodemirror().then((flag)=>{
          console.log("wtf")
          
        })
      }
      $('#param-modalexistingj').modal('show');    
  }

  updateParams(){
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.selectedTab;})[0]
    let step = obj.boxes.filter((item:any)=>{ return item.id == this.selectedStep})[0]
    let paramarr = []
    

    for (let index = 0; index < this.selectedStepParams.params.length; index++) {
      const key = this.selectedStepParams.params[index];
      if(key.type != "sourcecode"){
        let vlue = $("#param-"+key.name+"-value").val()
        paramarr.push({key:key.name,value:vlue,type:key.type})
      } else {
        let vlue:string = this.editor.getValue()
        paramarr.push({key:key.name,value:vlue,type:key.type})
      }
    }

    if(this.selectedStepParams.opt){
      for (let index = 0; index < this.selectedStepParams.opt.length; index++) {
        const key = this.selectedStepParams.opt[index];
        if(key.type != "sourcecode"){
          let vlue = $("#param-"+key.name+"-value").val()
          paramarr.push({key:key.name,value:vlue,type:key.type})
        } else {
          let vlue:string = this.editor.getValue()
          paramarr.push({key:key.name,value:vlue,type:key.type})
        }
      }
    }
    step.params = paramarr
    $('.param-value-input').val('');
    $('#param-modalexistingj').modal('hide');
    console.log(paramarr)
  }
  removeStep(){
    let obj = this.data.dags.filter(( obj:any )=> {return  obj.name == this.selectedTab;})[0]
    let indecr = 0
    for (let index = 0; index < this.data.dags.length; index++) {
      const element = this.data.dags[index];
      if( obj.name == this.selectedTab){
        indecr = index;
      }
    }
    let index1 = 0
    for (let index = 0; index < obj.boxes.length; index++) {
      const box = obj.boxes[index];
      if(box.id == this.selectedStep){
        index1 = index
      }
    }
    if(this.data.dags[indecr].boxes[index1-1] && this.data.dags[indecr].boxes[index1+1]){
      this.data.dags[indecr].boxes[index1+1].source = this.data.dags[indecr].boxes[index1].source
    }
    this.data.dags[indecr].boxes.splice(index1, 1);
    this.removeStepEvent.emit(obj);
    this.close()
    /*this.diagram.clear()
    this.redraw(obj,this.diagram)*/
  }
  close(){
    $('#param-modalexistingj').modal('hide');
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
