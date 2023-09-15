import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { NewJInputPort } from 'src/app/application/inputs/mewj.input.port';
import {Buffer} from 'buffer';
import { DagPropsComponent } from '../../base/dag-props/dag-props.component';
import { DagCanvasComponent } from '../../base/dag-canvas/dag-canvas.component';
import { ParamExistingjComponent } from '../param-existingj/param-existingj.component';



declare var $:any
declare var joint:any;
declare var dagre:any
@Component({
  selector: 'app-newj',
  templateUrl: './newj.component.html',
  styleUrls: ['./newj.component.css']
})
export class NewjComponent {

  
  @ViewChild("jarnameinput") jarnameinput!:ElementRef;
  @ViewChild("dagPropsComponent") dagProps!:DagPropsComponent;
  @ViewChild("dagCanvasComponent") dagCanvas!:DagCanvasComponent;
  @ViewChild("modalparam") modalparam!:ParamExistingjComponent;
  
  
  
  data:any = {
    jarname: "",
    dags:[]
  }
  
  
  

  parameters:any[] = []
  boxes: any = []
  temporaljarname:any = "dagJar1.jar"
  diagram:any 
  selectedTab:string = ""
  selectedObj!:any

  

  constructor(private router: Router,private service: NewJInputPort){}

  ngOnInit(): void {
    this.service.getOperatorMetadata().then((r:any)=>{
      this.parameters = JSON.parse(r)
      this.dagCanvas.setupViewer().then((g:any)=>{
        this.diagram = g
        this.data.jarname = this.temporaljarname
      })
    })
  }
  changeTab(dagname:any){
    this.selectedTab = dagname
    let obj = this.data.dags.filter(( obj:any )=> {return obj.name == dagname;})[0]
    this.boxes = obj.boxes
    this.diagram.clear()
    this.redraw(obj,this.diagram)
  }
  clickedStep(event:any){
    this.selectedObj = event
    this.modalparam.show()
  }
  redraw(obj:any,g:any){
    if(obj && obj.boxes){

      for (let i = 0; i < obj.boxes.length; i++) {
        const targetBox = obj.boxes[i].rect;
        const sourceBox = obj.boxes[i].source;
        targetBox.addTo(g);
        const link = new joint.shapes.standard.Link({router: { name: 'manhattan' }});
        
        if(sourceBox){
          
          link.source({ id: sourceBox.id });
          link.target({ id: targetBox.id });

          if(obj.boxes[i].status == "ERROR"){
            link.attr('line/stroke', 'red');
          } else if(obj.boxes[i].status == "OK"){
            link.attr('line/stroke', 'green');
          } else {
            link.attr('line/stroke', 'black');
          }
          

        }
        g.addCell(link);
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
  changeJarName(){
    this.temporaljarname = this.jarnameinput.nativeElement.value
    this.data.jarname = this.temporaljarname
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
  
  async createNewStep(tabname:string){  
      this.dagProps.saveDag(tabname);
      let obj = this.data.dags.filter(( obj:any )=> {return obj.name == $("#stepinput-"+tabname).val();})[0]
      this.redraw(obj,this.diagram)
  }
  
  async saveJar(){
    this.changeJarName()
    var base64 = Buffer.from(JSON.stringify(this.data)).toString('base64')
    try {
      await this.service.createUncompiled(base64)  
      this.router.navigateByUrl("auth/jobs");
    } catch (error) {
      alert(error)
    }
  }
  
  changeDagName(newname:any){
    this.selectedTab = newname
  }
  saveDag(obj:any){
    obj.boxes = this.boxes
    this.changeTab(obj.name)
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
  removeStepEvent(event:any){
    this.diagram.clear()
    this.redraw(event,this.diagram)
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
    this.saveJar()
  }
  execStepEvent(event:any){
    alert("save your DAG first")
  }
}
