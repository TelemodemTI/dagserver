import { Component, EventEmitter, Input, Output } from '@angular/core';
declare var $:any
declare var joint:any;
@Component({
  selector: 'app-dag-canvas',
  templateUrl: './dag-canvas.component.html',
  styleUrls: ['./dag-canvas.component.css']
})
export class DagCanvasComponent {
  generatedIdParams:any[] = []
  selectedStep:string = ""
  selectedStepParams!:any
  @Input('data') data!: any
  @Input('parameters') parameters!: any
  @Input('selectedTab') selectedTab!: string
  @Output() clickedStepEvent = new EventEmitter<any>();

  setupViewer(){
    let root = this
    $("#canvas-new-det").on("change", function() {
      var fromSelenium = $("#canvas-new-det").val();
      let json = JSON.parse(fromSelenium);

      let obj = root.data.dags.filter(( obj:any )=> {return obj.name == root.selectedTab})[0]
      let step = obj.boxes.filter((obj:any)=>{ return obj.id == json.stepname})[0]

      root.triggerClick(step)
    })
    return new Promise((resolve:any,reject:any)=>{
      setTimeout(()=>{
        var namespace = joint.shapes;
        let graph = new joint.dia.Graph({}, { cellNamespace: namespace });
        let paper = new joint.dia.Paper({
            el: document.getElementById("diagram-ctn-design"),
            model: graph,
            width: '100%',
            height: 600,
            gridSize: 2,
            drawGrid: true,
            restrictTranslate: true,
            cellViewNamespace: namespace,
            interactive: true
        });    
        $("#graph-lineage-opt-form").draggable({revert: "invalid"});
        $("#diagram-ctn-design").droppable()
        paper.on('element:pointerdblclick', (elementView:any)=> {
          let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.selectedTab})[0]
          let step = obj.boxes.filter((obj:any)=>{ return obj.id == elementView.model.attributes.attrs.label.text})[0]
          root.triggerClick(step)
        });
        resolve(graph)
      },100)
    })
  }
  triggerClick(step:any){
    this.selectedStepParams = this.parameters.filter((el:any)=>{ return el.class == step.type })[0]
    this.generatedIdParams = []
    if(this.selectedStepParams){
      this.selectedStepParams.params.forEach((el:any)=>{
        let defval = undefined
        let opt = el.opt?el.opt:[]
        if(step.params){
            let parit = step.params.filter((ela:any)=> ela.key == el.name)[0]
            defval = parit.value
        }
        this.generatedIdParams.push({key:el.name,type:el.type,value:defval,source:"PAR",domid:this.generateRandomString(5),opt:opt})
      })
    }
    if(this.selectedStepParams && this.selectedStepParams.opt){
      this.selectedStepParams.opt.forEach((el:any)=>{
        let defval = undefined
        let opt = el.opt?el.opt:[]
        if(step.params){
          let  found = step.params.filter((ela:any)=> ela.key == el.name)
          if(found.length > 0){
			      let parit = found[0]
          	defval = parit.value  
		      } else {
            defval = ""
          }
          
        }
        this.generatedIdParams.push({key:el.name,type:el.type, value:defval,source:"OPT", domid:this.generateRandomString(5),opt:opt})
      })
    }
    this.selectedStep = step.id
    this.clickedStepEvent.emit({selectedStep:this.selectedStep,selectedStepParams:this.selectedStepParams,generatedIdParams:this.generatedIdParams})
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
