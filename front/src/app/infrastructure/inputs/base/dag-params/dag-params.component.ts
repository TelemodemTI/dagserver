import { Component, ElementRef, Input, SimpleChanges, ViewChild } from '@angular/core';

declare var $:any
@Component({
  selector: 'app-dag-params',
  templateUrl: './dag-params.component.html',
  styleUrls: ['./dag-params.component.css']
})
export class DagParamsComponent {
  
  @Input('dagName') dagName!: string;
  @Input('data') data!: any
  @Input('boxes') boxes!: any
  @Input('timestamp') timestamp!: any
  @ViewChild("dagsavedstatus") dagsavedstatus!:ElementRef;
  
  params:any[] = []

  ngOnChanges(changes: SimpleChanges) {
    this.params = this.boxes.map((elem:any)=>{ 
      let tmp = this.data.jarname+"."+elem.id+"."+elem.type
      if(elem.params){
        let rv = elem.params.map((elem2:any)=>{
          elem2.step = elem.id
          elem2.rkey = tmp + "."+elem2.source+"."+elem2.key
          return elem2
        })
        return rv
      }  
    }).flat(1)
  }
  
  collapse(dagname:any){
    let flag = ($("#props-collapser-"+dagname).attr("aria-expanded").toLowerCase() === 'true')?true:false;
    let flags = ($("#props-collapser-son-"+dagname).attr("aria-expanded").toLowerCase() === 'true')?true:false;
    if(flags && !flag){
      setTimeout(()=>{
        $("#props-collapser-son").trigger("click");
      },50)
    }
  }
  copyPropJSON(){
    let jsonobj:any = {}
    this.params.forEach((elem:any)=>{
      if(elem.source=="props"){
        jsonobj[elem.rkey]=elem.value
      }
    })
    this.copyToClipboard(JSON.stringify(jsonobj))
    alert("JSONObject copied to clipboard")
  }
  copyOptsJSON(){
    let jsonobj:any = {}
    this.params.forEach((elem:any)=>{
      if(elem.source=="opts"){
        jsonobj[elem.rkey]=elem.value
      }
    })
    this.copyToClipboard(JSON.stringify(jsonobj))
    alert("JSONObject copied to clipboard")
  }
  copyToClipboard(item:string): void {
    let listener = (e: ClipboardEvent) => {
        e.clipboardData!.setData('text/plain', (item));
        e.preventDefault();
    };
    document.addEventListener('copy', listener);
    document.execCommand('copy');
    document.removeEventListener('copy', listener);
}
}
