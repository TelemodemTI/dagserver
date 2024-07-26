import { ChangeDetectorRef, Component, Input, SimpleChanges } from '@angular/core';
declare var $:any
declare var CodeMirror:any
@Component({
  selector: 'app-source-type-param',
  templateUrl: './source-type-param.component.html',
  styleUrls: ['./source-type-param.component.css']
})
export class SourceTypeParamComponent {
  @Input("generatedIdParams") generatedIdParams:any
  editor!:any

  constructor(private cd: ChangeDetectorRef){}

  ngOnChanges(changes: SimpleChanges) {
    let root = this
    if(this.generatedIdParams){
      this.initCodemirror().then((flag)=>{
        $("#canvas-codemirror-new-det").on("change", function() {
          var b64 = $("#canvas-codemirror-new-det").val();
          var fromSelenium = atob(b64)
          root.editor.setValue(fromSelenium);
        })
      })
    }
  }

  initCodemirror(){
    return new Promise((resolve,reject)=>{
      setTimeout(()=>{
        var width = $("#queryTextqv").attr("width");
        var height = $("#queryTextqv").attr("height");
        var read = $("#queryTextqv").data("readonly"); 
        var lenguaje = $("#canvas-codemirror-mode").val(); 
        var lineWrapping = (read)?true:false;
        var mime = (lenguaje)?lenguaje:"text"
        console.log(mime)
        try {
          var obj = document.getElementById("queryTextqv")
          if(obj){
            this.editor = CodeMirror.fromTextArea(obj, {
                  lineNumbers: true,
                  lineWrapping: lineWrapping,
                  readOnly: read,
                  matchBrackets: true,
                  mode: mime,
                  continueComments: "Enter"
            })
            this.editor.setSize(width,height)  
            this.editor.refresh();  
            console.log(this.editor)
          }
        } catch (error) {
          console.log(error)
          console.log("error en codemirror loading")
        }
        
        resolve(true)
      },10)
    })
  }
  refreshCodemirror(){
    let interval = setInterval(()=>{
        clearInterval(interval)
        setTimeout(() => {
          this.editor.refresh()
          this.cd.detectChanges(); 
        }, 300); 
    },100)
  }
  show(){
    if(!this.editor){
      this.initCodemirror().then((flag)=>{
        console.log("wtf")
      })
    }
    $('#param-modalexistingj').modal('show');    
  }
  getValue(){
    return this.editor.getValue()
  }
  setValue(value:any){
    if(this.editor){    
      this.editor.setValue(value) 
    }
  }
}
