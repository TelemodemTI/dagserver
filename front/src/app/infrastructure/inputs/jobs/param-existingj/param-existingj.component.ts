import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild, ChangeDetectorRef, Renderer2 } from '@angular/core';
import { DefaultTypeParamComponent } from '../../param-editor/default-type-param/default-type-param.component';
import { SourceTypeParamComponent } from '../../param-editor/source-type-param/source-type-param.component';
import { RemoteTypeParamComponent } from '../../param-editor/remote-type-param/remote-type-param.component';
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

  @ViewChild("inputDefault") inputDefault!:DefaultTypeParamComponent;
  @ViewChild("inputSource") inputSource!:SourceTypeParamComponent;
  @ViewChild("inputRemote") inputRemote!:RemoteTypeParamComponent;
  
  
  


  @Input("data") data:any
  @Input("selectedTab") selectedTab:any
  @Input("generatedIdParams") generatedIdParams:any
  @Input("selectedStep") selectedStep:any
  @Input("selectedStepParams") selectedStepParams:any
  @Output() removeStepEvent = new EventEmitter<any>();
  @Output() loadFromStepEvent = new EventEmitter<any>();
  @Output() updateStepEvent = new EventEmitter<any>();
  @Output() execStepEvent = new EventEmitter<any>();

  
  disabledChanges:any = {}
  disabledChecklist:any = {}
  another:any[] = []
  name!:any
  statusSel!:any
  xcoms:any[] = []
  disabledrm:boolean = true;
  
  private keydownListener!: () => void;

  constructor(private cd: ChangeDetectorRef, private renderer: Renderer2) {}

  handleKeyDown(event: KeyboardEvent) {
    console.log(event)
    if (event.ctrlKey && (event.key === 's' || event.key === 'S')) {
      event.preventDefault();
      this.updateParams();
    }
  }
  ngOnChanges(changes: SimpleChanges) {
    let root = this
    if(this.generatedIdParams){
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
            let value,variab;
            try {
              if(step.params){
                let valarr = step.params.filter((ele:any)=>{ return ele.type == "sourcecode" })
                if(valarr.length > 0){
                  value = valarr[0]
                  this.inputSource.setValue(value.value)
                }
                let variabarr = step.params.filter((ele:any)=>{ return ele.type == "remote" })
                if(variabarr.length > 0){
                  variab = variabarr[0];
                  this.inputRemote.setValue(variab.value)
                }
              }

            } catch (error) {
              console.log(error)
				      console.log("error controlado?")
			      }
            const activeTabId = this.tabIsActive();
            $("#settings_li > a").click();
            setTimeout(()=>{
              $(activeTabId + " > a").click();
              this.disabledrm = false;
            },250)
          }
        }
    }
  }
  loadFrom(id:any){
    let target = this.another.filter((elem:any)=>{ return elem.id == id})[0]
    this.loadFromStepEvent.emit(target)
  }
  
  show(){
    this.disabledrm = true;
    this.loader?.nativeElement.classList.remove("invisible");
    this.form?.nativeElement.classList.add("invisible")
    this.inputSource.show();
    $('#param-modalexistingj').modal('show');    
    this.changeTab("#settings");
    this.keydownListener = this.renderer.listen('document', 'keydown', (event: KeyboardEvent) => this.handleKeyDown(event));
    
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
    if (this.keydownListener) {
      this.keydownListener();
    }
  }
  loadParams(){
    let paramarr = []
    if(this.selectedStepParams){
      console.log(this.selectedStepParams)
      for (let index = 0; index < this.selectedStepParams.params.length; index++) {
        const key = this.selectedStepParams.params[index];
        if(key.type == "sourcecode"){
          let vlue:string = this.inputSource.getValue();
          paramarr.push({key:key.name,value:vlue,type:key.type,source:"props"})
        } else if(key.type == "remote"){
          paramarr.push({key:key.name,value:this.inputRemote.getValue(),type:key.type,source:"props"})
        } else {
          let vlue = $("#param-"+key.name+"-value").val()
          paramarr.push({key:key.name,value:vlue,type:key.type,source:"props"})
        }
      }
    }
    if(this.selectedStepParams && this.selectedStepParams.opt){
      for (let index = 0; index < this.selectedStepParams.opt.length; index++) {
        const key = this.selectedStepParams.opt[index];
        if(key.type == "sourcecode"){
          let vlue:string = this.inputSource.getValue()
          paramarr.push({key:key.name,value:vlue,type:key.type,source:"opts"})
        } else if(key.type == "remote"){
          paramarr.push({key:key.name,value:this.inputRemote.getValue(),type:key.type,source:"opts"})
        } else {
          let vlue = $("#param-"+key.name+"-value").val()
          paramarr.push({key:key.name,value:vlue,type:key.type,source:"opts"})
        }
      }
    }
    return paramarr;
  }
  changeTab(jid:string){
    $(".param-editor").removeClass("in active")
    $(".param-editor").addClass("noDisplay")
    $(jid).addClass("in active")
    $(jid).removeClass("noDisplay")
    if(jid=="#profile"){
      this.inputSource.refreshCodemirror()
    }  
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
    if (this.keydownListener) {
      this.keydownListener();
    }
    this.disabledrm = true;
    $('#param-modalexistingj').modal('hide');
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
      $("#param-"+item.name+"-value").val("")
      item.value = ""
      delete this.disabledChanges[item.key]
    } else {
      if(this.disabledChanges[item.key]){
        $("#param-"+item.name+"-value").val("")
        item.value = ""
        delete this.disabledChanges[item.key]
      } else {
        if(item.value){
          $("#param-"+item.name+"-value").val("")
          item.value = ""
          delete this.disabledChanges[item.key]
        } else {
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
    return "#settings_li"
  }
  ngAfterContentChecked() {
    this.cd.detectChanges();
  }
  tabIsDisplayed(jid:string){ 
    if(jid=="#profile"){
      return this.generatedIdParams?this.generatedIdParams.filter((elem:any)=> elem.type == "sourcecode").length > 0:false
    } else if(jid=="#remoter"){
      return this.generatedIdParams?this.generatedIdParams.filter((elem:any)=> elem.type == "remote").length > 0:false
    } else {
      return $(jid).text().trim()?true:false;
    }
  }
  
}
