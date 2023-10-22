import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DependenciesInputPort } from 'src/app/application/inputs/dependencies.input.port';

declare var $:any
declare var joint:any;
declare var dagre:any
declare var window:any
@Component({
  selector: 'app-dependencies',
  templateUrl: './dependencies.component.html',
  styleUrls: ['./dependencies.component.css']
})
export class DependenciesComponent {

  jarname:any = "";
  dagname:any = "";
  data!:any
  g!:any

  constructor(
    private router: Router, 
    private service: DependenciesInputPort,
    private route: ActivatedRoute){
      
    }

  async ngOnInit() {
    this.jarname = this.route.snapshot.paramMap.get('jarname');
    this.dagname = this.route.snapshot.paramMap.get("dagname")
    this.g = await this.initui()
    this.data = await this.service.getDependencies(this.jarname,this.dagname)
    this.draw()
  }

  async getDagImage(){
    //let base = (window['base-href'].startsWith("/auth/"))?"/":window['base-href']
    let base = "/"
    const segmentos = base.split('/');
    segmentos.pop();
    let rutaBase = segmentos.join('/');
    rutaBase = (rutaBase)?rutaBase:"/"
    rutaBase = rutaBase.endsWith("/")?rutaBase:rutaBase+"/"
    return rutaBase + "assets/images/operators/dag.png"
  }

  async draw(){
    let icon = await this.getDagImage()
    let rectactual = this.getShapeWithImage(this.dagname,icon)  
    rectactual.addTo(this.g);  
    for (let index = 0; index < this.data.onStart.length; index++) {
      const element = this.data.onStart[index].split(".");
      let rect = this.getShapeWithImage(element[1],icon)  
      rect.addTo(this.g);  
      const link = new joint.shapes.standard.Link({router: { name: 'manhattan' }});
      link.source({ id: rect.id });
      link.target({ id: rectactual.id });
      this.g.addCell(link);
    }
    for (let index = 0; index < this.data.onEnd.length; index++) {
      const element = this.data.onEnd[index].split(".");
      let rect = this.getShapeWithImage(element[1],icon)  
      rect.addTo(this.g);  
      const link = new joint.shapes.standard.Link({router: { name: 'manhattan' }});
      link.source({ id: rectactual.id });
      link.target({ id: rect.id });
      this.g.addCell(link);
    }
    try {
      
      dagre.layout()
      .nodeSep(150)
      .edgeSep(150)
      .rankSep(150)
      .rankDir("LR")   
      .run(); 
      joint.layout.DirectedGraph.layout(this.g, { setLinkVertices: true });
    } catch (error) {
      console.log("not initialized!")
    }
  }

  initui(){
      return new Promise((resolve:any,reject:any)=>{
        setTimeout(()=>{
          var namespace = joint.shapes;
          let graph = new joint.dia.Graph({}, { cellNamespace: namespace });
          let paper = new joint.dia.Paper({
              el: document.getElementById("diagram-ctn-dependencie"),
              model: graph,
              width: '90%',
              height: 600,
              gridSize: 2,
              drawGrid: true,
              restrictTranslate: true,
              cellViewNamespace: namespace,
              interactive: true
          });    
          $("#graph-lineage-opt-form").draggable({revert: "invalid"});
          $("#diagram-ctn-dependencie").droppable()
          paper.on('element:pointerdblclick', (elementView:any)=> {
            let dagname = elementView.model.attributes.attrs.label.text
            for (let index = 0; index < this.data.onEnd.length; index++) {
              const element = this.data.onEnd[index].split(".");
              if(dagname == element[1]){
                this.router.navigateByUrl(`auth/jobs/jarname/${element[0]}/${element[1]}`);
                break
              }
            }
            for (let index = 0; index < this.data.onStart.length; index++) {
              const element = this.data.onStart[index].split(".");
              if(dagname == element[1]){
                this.router.navigateByUrl(`auth/jobs/jarname/${element[0]}/${element[1]}`);
                break
              }
            }
            this.router.navigateByUrl(`auth/jobs/jarname/${this.jarname}/${this.dagname}`);
          })
          resolve(graph)
        },100)
    })  
  }
  getShapeWithImage(element:any,  imageUrl:string) {
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
                text: element,
                fill: 'black'
            }
    });
      return shape
}

randomIntFromInterval(min:any, max:any) { // min and max included 
  return Math.floor(Math.random() * (max - min + 1) + min)
}

}
