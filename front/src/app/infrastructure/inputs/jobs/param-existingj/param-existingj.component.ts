import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild, ChangeDetectorRef } from '@angular/core';
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
  @ViewChild("stagenameinput") stagenameinput!:ElementRef;
  @ViewChild("linkstatusinput") linkstatusinput!:ElementRef;

  @Input("data") data:any
  @Input("selectedTab") selectedTab:any
  @Input("generatedIdParams") generatedIdParams:any
  @Input("selectedStep") selectedStep:any
  @Input("selectedStepParams") selectedStepParams:any
  @Output() removeStepEvent = new EventEmitter<any>();
  @Output() loadFromStepEvent = new EventEmitter<any>();
  @Output() updateStepEvent = new EventEmitter<any>();
  @Output() execStepEvent = new EventEmitter<any>();

  editor!:any
  disabledChanges:any = {}
  disabledChecklist:any = {}
  another:any[] = []
  name!:any
  statusSel!:any
  xcoms:any[] = []

  constructor(private cd: ChangeDetectorRef){

  }

  ngOnChanges(changes: SimpleChanges) {
    let root = this
    if(this.generatedIdParams){
      this.initCodemirror().then((flag)=>{
        this.loader?.nativeElement.classList.add("invisible");
        this.form?.nativeElement.classList.remove("invisible")
        if(this.data){
          let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.selectedTab;})[0]
          if(obj){
            let step = obj.boxes.filter((item:any)=>{ return item.id == this.selectedStep})[0]
            this.another = obj.boxes.filter((elem:any)=>{ return elem && step && elem.type == step.type && elem.id != step.id})
            this.xcoms = obj.boxes.filter((elem:any)=>{ return elem && step && elem.id != step.id})
            this.name = step.id
            this.statusSel = step.status
            let value;
            try {
                value = step.params.filter((ele:any)=>{ return ele.type == "sourcecode" })[0]  
                if(this.editor){    
                  this.editor.setValue(value.value) 
                }
            } catch (error) {
				console.log(error)
			}
            const activeTabId = this.tabIsActive();
            $("#settings_li > a").click();
            setTimeout(()=>{
              $(activeTabId + " > a").click();
            },250)
          }
        }
        $("#canvas-codemirror-new-det").on("change", function() {
          var fromSelenium = $("#canvas-codemirror-new-det").val();
          root.editor.setValue(fromSelenium);
        })
    })
    }
  }
  loadFrom(id:any){
    let target = this.another.filter((elem:any)=>{ return elem.id == id})[0]
    this.loadFromStepEvent.emit(target)
  }
  refreshCodemirror(){
    let interval = setInterval(()=>{
      if(this.loader?.nativeElement.classList.contains("invisible")){
        clearInterval(interval)
        setTimeout(() => {
          this.editor.refresh()
          this.cd.detectChanges(); 
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
    let paramarr = this.loadParams()  
    step.params = paramarr
    let stagename = this.stagenameinput.nativeElement.value
    let statusLink = this.linkstatusinput.nativeElement.value
    this.updateStepEvent.emit({name:stagename,statusLink:statusLink,old:this.name})
    $('.param-value-input').val('');
    $('#param-modalexistingj').modal('hide');
  }
  loadParams(){
    let paramarr = []
    if(this.selectedStepParams){
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
    }
    if(this.selectedStepParams && this.selectedStepParams.opt){
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
    return paramarr;
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
          if(obj){
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
            console.log(this.editor)
          }
        } catch (error) {
          console.log(error)
          console.log("error en codemirror loading")
        }
        
        resolve(true)
      },10)
    })
  }
  isDisabled(item:any){
    if(item.source == "OPT"){
      if(item.value){
          return false
      } else { 
          if(this.disabledChanges[item.key]){
            return false
          } else {
            return true
          }
      }
    } else {
      return false
    }
  }
  check(item:any,ischecked:any){
    if(ischecked){
      console.log("poraki1")
      $("#param-"+item.name+"-value").val("")
      item.value = ""
      delete this.disabledChanges[item.key]
    } else {
      if(this.disabledChanges[item.key]){
        console.log("poraki2")
        $("#param-"+item.name+"-value").val("")
        item.value = ""
        console.log(item)
        delete this.disabledChanges[item.key]
      } else {
        if(item.value){
          console.log("poraki3")
          $("#param-"+item.name+"-value").val("")
          item.value = ""
          delete this.disabledChanges[item.key]
        } else {
          console.log("poraki")
          this.disabledChanges[item.key]=true
        }
      }
    }
  }
  isChecked(item:any){
    let vl :string = item.value
    return item.source == 'PAR' || vl != ""
  }
  isChlDisabled(item:any){
    return item.source == 'PAR'
  }
  execStep(){
    let opt = confirm("you sure?")
    if(opt){
      this.execStepEvent.emit({dagname:this.selectedTab,step:this.selectedStep})
    }
  }  
  tabIsActive(){
    //return this.tabIsDisplayed('#home')?'#home_li':(this.tabIsDisplayed('#profile')?'#profile_li':'#settings_li')
    return "#settings_li"
  }
  ngAfterContentChecked() {
    this.cd.detectChanges();
  }
  tabIsDisplayed(jid:string){ 
    if(jid=="#profile"){
      return this.generatedIdParams?this.generatedIdParams.filter((elem:any)=> elem.type == "sourcecode").length > 0:false
    } else {
      return $(jid).text().trim()?true:false;
    }
  }
}
