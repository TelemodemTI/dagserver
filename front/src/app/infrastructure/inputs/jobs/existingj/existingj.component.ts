import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ExistingJInputPort } from 'src/app/application/inputs/existingj.input.port';
import { Uncompileds } from 'src/app/domain/models/uncompiled.model';
import {Buffer} from 'buffer';
import { DagPropsComponent } from '../../base/dag-props/dag-props.component';
import { DagOpsComponent } from '../../base/dag-ops/dag-ops.component';
import { DagCanvasComponent } from '../../base/dag-canvas/dag-canvas.component';
import { ParamExistingjComponent } from '../param-existingj/param-existingj.component';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';
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
  @ViewChild("modalparam") modalparam!:ParamExistingjComponent;
  @ViewChild("modalparamv") vlmod!:ValueModalComponent;  


  parameters: any[] = []
  boxes: any = []
  uncompiled!:any
  item:any
  diagram:any 
  data!:any
  selectedTab:string = ""
  selectedObj!:any
  

  constructor(private router: Router, 
    private route: ActivatedRoute,
    private service: ExistingJInputPort){
  }

  async ngOnInit() {
    this.uncompiled = this.route.snapshot.paramMap.get('uncompiledId');
    let arr = await this.service.getUncompileds()
    this.item = arr.filter((el:Uncompileds)=>{ return (el.uncompiledId == this.uncompiled)})[0]
    console.log(this.item)
    this.data = JSON.parse( this.item.bin)
    
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
  
  async saveJar(){
    var base64 = Buffer.from(JSON.stringify(this.data)).toString('base64')
    try {
      await this.service.saveUncompiled(parseInt(this.uncompiled),base64)  
      this.router.navigateByUrl("auth/jobs");
    } catch (error) {
      alert(error)
    }
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
    this.selectedTab = dagname

    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == dagname;})[0]    
    this.boxes = obj.boxes
    this.diagram.clear()
    this.redraw(obj,this.diagram)
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
    this.dagProps.saveDag(tabname);
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == $("#stepinput-"+tabname).val();})[0]
    this.redraw(obj,this.diagram)
  }

  saveDag(obj:any){
    obj.boxes = this.boxes
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
    await this.service.renameUncompiled(this.uncompiled,event[1])
    this.router.navigateByUrl("auth/jobs");
  }
}
