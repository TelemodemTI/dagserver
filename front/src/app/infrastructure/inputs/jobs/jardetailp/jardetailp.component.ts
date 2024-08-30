import { Component, ElementRef, Input, Renderer2, SimpleChanges, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { JardetailpInputPort } from 'src/app/application/inputs/jardetailp.input.port';
import { DefaultTypeParamComponent } from '../../param-editor/default-type-param/default-type-param.component';
import { SourceTypeParamComponent } from '../../param-editor/source-type-param/source-type-param.component';
import { RemoteTypeParamComponent } from '../../param-editor/remote-type-param/remote-type-param.component';
import { FileTypeParamComponent } from '../../param-editor/file-type-param/file-type-param.component';
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
  @ViewChild("inputDefault") inputDefault!:DefaultTypeParamComponent;
  @ViewChild("inputSource") inputSource!:SourceTypeParamComponent;
  @ViewChild("inputRemote") inputRemote!:RemoteTypeParamComponent;
  @ViewChild("inputFile") inputFile!:FileTypeParamComponent;
  
  xcoms:any[] = []
  generatedIdParams:any[] = []
  
  private keydownListener!: () => void;

  constructor(private router: Router,private service: JardetailpInputPort,private renderer: Renderer2){
  }
  

  ngOnChanges(changes: SimpleChanges) {
    this.loader?.nativeElement.classList.add("invisible");
    this.form?.nativeElement.classList.remove("invisible")
    this.xcoms = this.selectedDag ? this.selectedDag.node : [];
    this.generatedIdParams = []
    if(this.selectedStepParams && this.selectedStepOpts){
      this.selectedStepMetadata.params.forEach((el:any)=>{
        this.generatedIdParams.push({key:el.name,type:el.type,value:this.selectedStepParams[el.name],source:"PAR",domid:this.generateRandomString(5),opt:[]})
      })
      this.selectedStepMetadata.opt.forEach((el:any)=>{
        this.generatedIdParams.push({key:el.name,type:el.type,value:this.selectedStepOpts[el.name],source:"OPT",domid:this.generateRandomString(5),opt:[]})
      })  
    }
  }
  changeTab(jid:string){
    $(".param-editor").removeClass("in active")
    $(".param-editor").addClass("noDisplay")
    $(jid).addClass("in active")
    $(jid).removeClass("noDisplay")
    if(jid=="#profile"){
      this.inputSource.refreshCodemirror()
    }  
    if(this.selectedStepMetadata){
      let paramarr = this.selectedStepMetadata.params.filter((ele:any)=>{ return ele.type == "sourcecode" })
      if(paramarr.length > 0){  
        this.inputSource.setValue(this.selectedStepParams[paramarr[0].name]) 
      }
      let variabarr = this.selectedStepMetadata.params.filter((ele:any)=>{ return ele.type == "remote" })
      if(variabarr.length > 0){
        this.inputRemote.setValue(this.selectedStepParams[variabarr[0].name])
      }
    }
    
  }
  close(){
    if (this.keydownListener) {
      this.keydownListener();
    }
    $('#param-modaljardetailj').modal('hide');
  }
  show(){
    this.loader?.nativeElement.classList.remove("invisible");
    this.form?.nativeElement.classList.add("invisible")
    $('#param-modaljardetailj').modal('show');
    this.keydownListener = this.renderer.listen('document', 'keydown', (event: KeyboardEvent) => this.handleKeyDown(event));
    this.changeTab("#home")
  }
  
  handleKeyDown(event: KeyboardEvent) {
    console.log(event)
    if (event.ctrlKey && (event.key === 's' || event.key === 'S')) {
      event.preventDefault();
      this.updateParams();
    }
  }
  async updateParams(){
    let paramarr = []
    for (let index = 0; index < this.selectedStepMetadata.params.length; index++) {
      const key = this.selectedStepMetadata.params[index];
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
    for (let index = 0; index < this.selectedStepMetadata.opt.length; index++) {
      const key = this.selectedStepMetadata.opt[index];
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
    let bin = btoa(JSON.stringify(paramarr))
    await this.service.updateParamsCompiled(this.jarname,this.selectedStep,this.selectedStepMetadata.class,bin)
    this.close()
    this.router.navigateByUrl("auth/jobs");
    
  }

  generateRandomString(length:number) {
      const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let result = '';
      for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * characters.length);
        result += characters.charAt(randomIndex);
      }
      return result;
    }
}
