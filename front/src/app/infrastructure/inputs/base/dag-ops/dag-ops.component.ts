import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { DagOpsInputPort } from 'src/app/application/inputs/dagops.input.port';
declare var $:any
declare var joint:any;
declare var window:any
@Component({
  selector: 'app-dag-ops',
  templateUrl: './dag-ops.component.html',
  styleUrls: ['./dag-ops.component.css']
})
export class DagOpsComponent {


  @Input('dagName') dagName!: string
  @Input('diagram') diagram!: any
  @Input('boxes') boxes!: any[]
  @Input('parameters') parameters!: any
  @Output() createNewStepEvent = new EventEmitter<string>();

  sshOperatorImg! : string;
  dummyOperatorImg! : string;
  groovyOperatorImg! : string;
  jdbcOperatorImg! : string;
    


  constructor(private service: DagOpsInputPort){}

  ngOnChanges(changes: SimpleChanges) {
    //let base = (window['base-href'].startsWith("/auth/"))?"/":window['base-href']
    let base = "/"
    const segmentos = base.split('/');
    segmentos.pop();
    let rutaBase = segmentos.join('/');
    rutaBase = (rutaBase)?rutaBase:"/"
    rutaBase = rutaBase.endsWith("/")?rutaBase:rutaBase+"/"

    this.sshOperatorImg = "url(" +rutaBase + "assets/images/operators/ssh.png)";
    this.dummyOperatorImg = "url(" +rutaBase + "assets/images/operators/dummy.png)";
    this.groovyOperatorImg = "url(" +rutaBase + "assets/images/operators/groovy.png)";
    this.jdbcOperatorImg = "url(" +rutaBase + "assets/images/operators/jdbc.png)";

  }
  
  async add(operator:any,tabname:string){
    let founded = this.boxes.filter((event:any)=>{
      return event.id == $("#stepinput-"+tabname).val()
    })
    if(founded.length == 0 && $("#stepinput-"+tabname).val()){
      let rect = this.getShapeWithImage($("#stepinput-"+tabname).val(),await this.getimageByType( operator))
      this.boxes.push({id:$("#stepinput-"+tabname).val(),type: operator,status:$("#status-"+tabname).val(),rect:rect, source: this.getsource( $("#sourcestep-"+tabname).val())});
      rect.addTo(this.diagram);
      $("#stepinput-"+tabname).val("")
      this.createNewStepEvent.emit(tabname)
    } else {
      alert("invalid step name")
    }
  }


  async createNewStep(tabname:string){
    let founded = this.boxes.filter((event:any)=>{
      return event.id == $("#stepinput-"+tabname).val()
    })
    if(founded.length == 0 && $("#stepinput-"+tabname).val()){
      let rect = this.getShapeWithImage($("#stepinput-"+tabname).val(),await this.getimageByType( $("#steptype-"+tabname).val()))
      this.boxes.push({id:$("#stepinput-"+tabname).val(),type: $("#steptype-"+tabname).val(),status:$("#status-"+tabname).val(),rect:rect, source: this.getsource( $("#sourcestep-"+tabname).val())});
      rect.addTo(this.diagram);
      $("#stepinput-"+tabname).val("")
      this.createNewStepEvent.emit(tabname)
    } else {
      alert("invalid")
    }
  }
  getShapeWithImage(label:any, imageUrl:string) {
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
                text: label,
                fill: 'black'
            }
    });
    return shape
  }
  randomIntFromInterval(min:any, max:any) { // min and max included 
    return Math.floor(Math.random() * (max - min + 1) + min)
  }
  
  async getimageByType(typeop:any){
    //let base = (window['base-href'].startsWith("/auth/"))?"/":window['base-href']
    let base = "/"
    const segmentos = base.split('/');
    segmentos.pop();
    let rutaBase = segmentos.join('/');
    rutaBase = (rutaBase)?rutaBase:"/"
    rutaBase = rutaBase.endsWith("/")?rutaBase:rutaBase+"/"
    return rutaBase + "assets/images/operators/" + await this.service.getIcons(typeop)
  }
  getsource(id:any){
    if(id){
      let founded = this.boxes.filter((event:any)=>{
        return event.id == id
      })
      if(founded.length>0){
        return founded[0].rect
      } else {
        return undefined
      }
    } else {
      return undefined
    }
  }
  collapse(dagname:any){
    try {
      let flag = ($("#props-collapser").attr("aria-expanded").toLowerCase() === 'true')?true:false;
      let flags = ($("#props-collapser-son").attr("aria-expanded").toLowerCase() === 'true')?true:false;
      if(flag && !flags){
        setTimeout(()=>{
          $("#props-collapser").trigger("click");
        },50)
  
      }  
    } catch (error) {
      console.log("notfound")
    }
    
  }
}
