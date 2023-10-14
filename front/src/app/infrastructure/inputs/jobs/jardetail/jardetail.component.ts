import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JardetailInputPort } from 'src/app/application/inputs/jardetail.input.port';
import { JardetailpComponent } from '../jardetailp/jardetailp.component';
import { LogsInputPort } from 'src/app/application/inputs/logs.input.port';


declare var $:any
declare var joint:any;
declare var dagre:any
declare var window:any
@Component({
  selector: 'app-jardetail',
  templateUrl: './jardetail.component.html',
  styleUrls: ['./jardetail.component.css']
})
export class JardetailComponent {

  jarname:any = "";
  dagname:any = "";
  error_msje:any;
  result!:any

  dagsavai:any[] = []

  selectedDag!:any
  selectedStep!:any
  selectedStepOpts!:any
  selectedStepParams!:any
  selectedStepMetadata!:any
  last!:any
  dagitem!:any
  dagparsed!:any

  @ViewChild("modalp") modalp!: JardetailpComponent;

  constructor(private router: Router, 
    private route: ActivatedRoute,
    private servicel: LogsInputPort,
    private service: JardetailInputPort){
  }

  async ngOnInit() {
    this.jarname = this.route.snapshot.paramMap.get('jarname');
    this.dagname = this.route.snapshot.paramMap.get("dagname")  
    this.result = await this.service.getDetail(this.jarname);  
    let logs = await this.servicel.logs(this.dagname)
    this.last = logs.reduce((a, b) => (a.execDt > b.execDt ? a : b),logs[0]);
    this.calculateAnaliticPane(this.dagname,this.last);
    try {
      this.initui(this.result);
    } catch (error) {
      this.error_msje = error
      console.log(error)
      $('#propertyNotFoundModal').modal('show');
    }
  }

  async calculateAnaliticPane(dagname:any,last:any){
    let timemarks:any[] = [];
    let waits:any[] = []
    try {
      if(last){
        let xcom = JSON.parse(last.outputxcom)
        if(last && last.marks){
          let times = JSON.parse(last.marks).map((ele:any)=>{
            return new Date(parseInt(ele));
          })
          timemarks = times.map((ele:any)=>{
            return ele.toISOString();
          })
          for (let index = 0; index < times.length; index++) {
            if(times[index-1]){
              const diffTime = Math.abs(times[index] - times[index-1]);
              waits.push(diffTime)
            } else {
              waits.push(0)
            }
          }
          this.dagitem = this.result.detail.detail.filter((el:any)=> { return el.dagname == dagname })[0]
          let index = 0
          this.dagparsed = this.dagitem.node.map((ele:any)=>{
            let arr = ele.operations[1].split(".")
            index ++;
            let counted = xcom[ele.operations[0]] ? xcom[ele.operations[0]].length : 0
            return {
              name: ele.operations[0],
              operator: arr[arr.length-1],
              endOn: timemarks[index-1],
              wait: waits[index-1],
              rows: counted
            }
          })
        } else {
          this.dagparsed = []
        }
      }
    } catch (error) {
      console.log(error)
      console.log("no log info..")
    }
  }
  reinit(i:number){
    let item = this.dagsavai[i];
    this.calculateAnaliticPane(item,this.last);
    let arr = []
    for (let index = 0; index < this.result.detail.detail.length; index++) {
      const element = this.result.detail.detail[index];
      arr.push(this.setupViewer(element.dagname))
    }
    let diagrams :any[]= []
    let res = arr.reduce((prev:any,next:any)=>{
      return prev.then((res1:any)=> {
        diagrams.push(res1)
        return next()
      });
    },Promise.resolve())
    res.then((g:any)=>{
      diagrams.push(g)
      diagrams = diagrams.filter(item => item);
      for (let index = 0; index < diagrams.length; index++) {
        const diagram = diagrams[index];
        this.loadBoxes(diagram,this.result,index).then((boxes:any)=>{
          this.drawDiagram(diagram,boxes);
        })
      }
    })
  }
  async getimageByType(typeop:any){
    let base = (window['base-href'].startsWith("/auth/"))?"/":window['base-href']
    const segmentos = base.split('/');
    segmentos.pop();
    let rutaBase = segmentos.join('/');
    rutaBase = (rutaBase)?rutaBase:"/"
    rutaBase = rutaBase.endsWith("/")?rutaBase:rutaBase+"/"
    return rutaBase + "assets/images/operators/" + await this.service.getIcons(typeop)
  }
  

  getShapeWithImage(element:any, boxes:any[], imageUrl:string) {
    var shape = new joint.shapes.standard.Image();
    let left = this.randomIntFromInterval(0, 600);
    let top = this.randomIntFromInterval(0, 400);
    shape.resize(100, 40);
    shape.position(left, top);
    shape.attr({
            image: {
                'xlink:href': imageUrl
            },
            rect: {
                fill: "#42C1C1",
                rx: 5,
                ry: 5,
            },
            label: {
                text: element.operations[0],
                fill: 'black'
            }
    });
      return shape
}

  async loadBoxes(graph:any,result:any,dagIndex:any){
    let boxes: any = []
    for (let index = 0; index < result.detail.detail[dagIndex].node.length; index++) {
      const element = result.detail.detail[dagIndex].node[index];
      let rect = this.getShapeWithImage(element,boxes,await this.getimageByType(element.operations[1]))
      console.log(element)
      rect.addTo(graph);  
      boxes.push({id:element.index,rect:rect});
    }
    return boxes;
  }

  initui(result:any){
    let root = this
    let arr = []
    for (let index = 0; index < result.detail.detail.length; index++) {
      const element = result.detail.detail[index];
      this.dagsavai.push(element.dagname)
      arr.push(this.setupViewer(element.dagname))
    }
    let diagrams :any[]= []
    let res = arr.reduce((prev:any,next:any)=>{
      return prev.then((res1:any)=> {
        diagrams.push(res1)
        return next()
      });
    },Promise.resolve())
    res.then((g:any)=>{
      diagrams.push(g)
      diagrams = diagrams.filter(item => item);
      for (let index = 0; index < diagrams.length; index++) {
        const diagram = diagrams[index];
        
        this.loadBoxes(diagram,result,index).then((boxes)=>{
          this.drawDiagram(diagram,boxes);
        })
      }
      $("#canvas-det").on("change", function() {
        var fromSelenium = $("#canvas-det").val();
        let json = JSON.parse(fromSelenium);
        root.triggerClick(json.dagname,json.selectedStep)
      })
    })
  }

  drawDiagram(graph:any,boxes:any[]){
    for (let i = 0; i < boxes.length - 1; i++) {
      const sourceBox = boxes[i].rect;
      const targetBox = boxes[i+1].rect;
      // Crear un nuevo objeto Link y establecer la conexión entre las cajas
      const link = new joint.shapes.standard.Link({router: { name: 'manhattan' }});
      link.source({ id: sourceBox.id });
      link.target({ id: targetBox.id });
    
      // Agregar el objeto Link al grafo
      graph.addCell(link);
    }
    try {
      
      dagre.layout()
      .nodeSep(150)
      .edgeSep(150)
      .rankSep(150)
      .rankDir("LR")   
      .run(); 
      joint.layout.DirectedGraph.layout(graph, { setLinkVertices: true });
    } catch (error) {
      console.log("not initialized!")
    }
  }

  randomIntFromInterval(min:any, max:any) { // min and max included 
    return Math.floor(Math.random() * (max - min + 1) + min)
  }

  setupViewer(id:any){
    return () => new Promise((resolve:any,reject:any)=>{
      setTimeout(()=>{
        var namespace = joint.shapes;
        let graph = new joint.dia.Graph({}, { cellNamespace: namespace });
        let paper = new joint.dia.Paper({
            el: document.getElementById("diagram-ctn-"+id),
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
        $("#diagram-ctn-"+id).droppable()
        paper.on('element:pointerdblclick', (elementView:any)=> {
          let dagname = id;
          let selectedStep = elementView.model.attributes.attrs.label.text
          this.triggerClick(dagname,selectedStep)
        })
        resolve(graph)
      },100)
    })
  }
  triggerClick(dagname:any,selectedStep:any){
    let dag = this.result.detail.detail.filter((element:any)=>{ return element.dagname == dagname })[0]
    let node = dag.node.filter((node1:any)=>{
      return node1.operations[0] == selectedStep
    })[0]
    let params = JSON.parse(node.operations[2])
    let opts = JSON.parse(node.operations[3])
    let metadata = JSON.parse(node.operations[4])
    this.selectedStepMetadata = metadata
    this.selectedDag = dag
    this.selectedStep = selectedStep
    this.selectedStepOpts = opts
    this.selectedStepParams = params
    this.modalp.show();
  }
  goToLogDetail(id:any){
    this.router.navigateByUrl(`auth/jobs/${this.dagname}/${id}`);
  }
}
