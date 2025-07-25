import { Component, Renderer2, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExistingJInputPort } from 'src/app/application/inputs/existingj.input.port';
import { Uncompileds } from 'src/app/domain/models/uncompiled.model';
import {Buffer} from 'buffer';
import { DagPropsComponent } from '../../base/dag-props/dag-props.component';
import { DagOpsComponent } from '../../base/dag-ops/dag-ops.component';
import { DagCanvasComponent } from '../../base/dag-canvas/dag-canvas.component';
import { ParamExistingjComponent } from '../param-existingj/param-existingj.component';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';
import { ResultStepModalComponent } from '../../base/result-step-modal/result-step-modal.component';
import { DagParamsComponent } from '../../base/dag-params/dag-params.component';
declare var $:any
declare var joint:any;
declare var dagre:any
@Component({
  selector: 'app-existingj',
  templateUrl: './existingj.component.html',
  styleUrls: ['./existingj.component.css']
})
export class ExistingjComponent {

  @ViewChild("dagPropsComponent") dagProps!:DagPropsComponent;
  @ViewChild("dagOpsComponent") dagOps!:DagOpsComponent;
  @ViewChild("dagCanvasComponent") dagCanvas!:DagCanvasComponent;
  @ViewChild("dagParamsComponent") dagParams!:DagParamsComponent;
  
  @ViewChild("modalparam") modalparam!:ParamExistingjComponent;
  @ViewChild("modalparamv") vlmod!:ValueModalComponent;  
  @ViewChild("resultStepModal") resultStepModal!:ResultStepModalComponent;  
  @ViewChild("valuer") valuer!:ValueModalComponent  

  parameters: any[] = []
  boxes: any = []
  uncompiled!:any
  item:any
  diagram:any 
  data!:any
  selectedTab:string = ""
  selectedObj!:any
  hasViewDetail:boolean = false
  timestamp : number = new Date().getTime();
  currDagname:string = "";
  currStepname:string = ""
  private keydownListener!: () => void;

  constructor(private router: Router, 
    private route: ActivatedRoute,
    private service: ExistingJInputPort,
    private renderer: Renderer2
    ){
  }

  async ngOnInit() {
    this.keydownListener = this.renderer.listen('document', 'keydown', (event: KeyboardEvent) => this.handleKeyDown(event));
    this.uncompiled = this.route.snapshot.paramMap.get('uncompiledId');
    let arr = await this.service.getUncompileds()
    this.item = arr.filter((el:Uncompileds)=>{ return (el.uncompiledId == this.uncompiled)})[0]
    this.data = JSON.parse(this.item.bin)
    let r = await this.service.getOperatorMetadata();
    this.parameters = JSON.parse(r)
    for (let index = 0; index < this.data.dags.length; index++) {
      const obj = this.data.dags[index];
      this.boxes = obj.boxes
      for (let index = 0; index < obj.boxes.length; index++) {
        const element = obj.boxes[index];
        let rect = this.dagOps.getShapeWithImage(element.id,await this.dagOps.getimageByType( element.type))
        element.rect = rect
      }  
    }
    this.diagram = await this.dagCanvas.setupViewer()
  }
  ngOnDestroy() {
    if (this.keydownListener) {
      this.keydownListener();
    }
  }
  handleKeyDown(event: KeyboardEvent) {
    if (event.ctrlKey && (event.key === 's' || event.key === 'S')) {
      event.preventDefault();
      this.saveJar();
    }
  }
  async saveJar(){
    this.dagProps.saveDag(this.selectedTab);
    var base64 = Buffer.from(JSON.stringify(this.data)).toString('base64')
    try {
      await this.service.saveUncompiled(parseInt(this.uncompiled),base64)  
      alert("File was updated correctly")
    } catch (error) {
      alert(error)
    }
  }
  close(){
    this.router.navigateByUrl("auth/jobs");
  }
  createDag(){
    let dintmp = this.generateRandomString(6)
    let newname = "DAG_"+ dintmp
    let dag = {
      name : newname,
      class: "",
      group: "",
      cron: "",
      boxes: []
    };
    this.data.dags = this.data.dags.filter(( obj:any )=> {return obj.name !== newname});
    this.data.dags.push(dag);
    this.selectedTab = (this.data.dags.length == 1) ? newname : this.selectedTab
  }

  changeTab(dagname:string){
    this.hasViewDetail = true
    this.selectedTab = dagname
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == dagname;})[0]    
	this.boxes = obj.boxes
    if(this.diagram){
      this.diagram.clear()
	  try {
		this.redraw(obj,this.diagram)
	  } catch(error){
		setTimeout(()=>{
			this.changeTab(dagname)
		},250)
	  }
      
    }
  }
  redraw(obj:any,g:any){
    if(obj && obj.boxes){
      for (let i = 0; i < obj.boxes.length; i++) {
        const targetBox = obj.boxes[i].rect;
        targetBox.addTo(g);
        if(obj.boxes[i].source){
          const link = new joint.shapes.standard.Link({router: { name: 'manhattan' }});
          let sourceBox
          if(obj.boxes[i].source.attrs && obj.boxes[i].source.attrs.label && obj.boxes[i].source.attrs.label.text){
            sourceBox = this.dagOps.getsource(obj.boxes[i].source.attrs.label.text)
          } else {
            sourceBox = obj.boxes[i].source;  
          }
          link.source({ id: sourceBox.id });
          link.target({ id: targetBox.id });
          if(obj.boxes[i].status == "ERROR"){
            link.attr('line/stroke', 'red');
          } else if(obj.boxes[i].status == "OK"){
            link.attr('line/stroke', 'green');
          } else {
            link.attr('line/stroke', 'black');
          }
          g.addCell(link);
        }
        
        try {
            dagre.layout()
            .nodeSep(150)
            .edgeSep(150)
            .rankSep(150)
            .rankDir("LR")   
            .run(); 
            joint.layout.DirectedGraph.layout(g, { setLinkVertices: true });  
        } catch (error) {
          console.log("not initialized!")
        }
      }
    }
  }

  async createNewStep(tabname:string){  
    this.saveDag({name:tabname});
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == $("#stepinput-"+tabname).val();})[0]
    this.redraw(obj,this.diagram)
    this.changeTab(tabname)
  }

  saveDag(dag:any){
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == dag.name})[0]
	dag.boxes = this.boxes
    this.changeTab(obj.name)
  }
  changeDagName(newname:any){
    this.selectedTab = newname
  }
  
  
  removeStepEvent(event:any){
    this.diagram.clear()
    this.redraw(event,this.diagram)
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
  clickedStep(event:any){
    this.selectedObj = event
    this.modalparam.show()
  }
  rename(){
    this.vlmod.show();
  }
  async changeValueEvent(event:any){
    await this.service.renameUncompiled(parseInt(this.uncompiled),event[1])
    this.router.navigateByUrl("auth/jobs");
  }
  loadFromStepEvent(event:any){
    for (let index = 0; index < this.selectedObj.generatedIdParams.length; index++) {
      const element = this.selectedObj.generatedIdParams[index];
      element.value = event.params.filter((ele:any)=>{ return ele.key == element.key})[0].value
    }
  }
  updateStepEvent(event:any){
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.selectedTab;})[0]    
    let item = obj.boxes.filter((elem:any)=>{ return elem.id == event.old})[0]
    item.id = event.name
    item.status = event.statusLink
    let sourceitem = obj.boxes.filter((elem:any)=>{ 
                  return elem.source && elem.source.attrs &&  elem.source.attrs.label && elem.source.attrs.label.text == event.old
    });
    if(sourceitem.length > 0 && sourceitem[0].source){
      sourceitem[0].source.attrs.label.text = event.name
    }
    this.timestamp = new Date().getTime();
  }
  async execStepEvent(item:any){
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == this.selectedTab;})[0]
    let step = obj.boxes.filter((item:any)=>{ return item.id == this.selectedObj.selectedStep})[0]
    let paramarr = this.modalparam.loadParams()  
    step.params = paramarr
    var base64 = Buffer.from(JSON.stringify(this.data)).toString('base64')
    await this.service.saveUncompiled(parseInt(this.uncompiled),base64)  
    this.currDagname = item.dagname
    this.currStepname = item.step
    this.modalparam.close()
    this.valuer.show()
  }
  async playDesignJob(){
    if(this.hasViewDetail){
      this.currDagname = this.selectedTab
      this.currStepname = ""
      this.valuer.show()
    } else {
      alert("you must select DAG implementation first!")
    }    
  }

  async changeValueEvent1(data1:any){
    this.valuer.close();
    try {
      let data = await this.service.executeDagUncompiled(this.uncompiled,this.currDagname,this.currStepname,data1[1]);    
      this.service.sendResultExecution(data);
      this.resultStepModal.show(data);
    } catch (error) {
      
    }
  }
}
