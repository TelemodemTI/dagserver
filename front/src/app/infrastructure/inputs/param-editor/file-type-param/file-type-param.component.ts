import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ExplorerInputPort } from 'src/app/application/inputs/explorer.input.port';
declare var $:any
@Component({
  selector: 'app-file-type-param',
  templateUrl: './file-type-param.component.html',
  styleUrls: ['./file-type-param.component.css']
})
export class FileTypeParamComponent implements OnChanges {
  
  @Input("generatedIdParams") generatedIdParams:any
  showFlag = false
  showExplorer = false
  items!:any
  mounted!:any
  data:any[] = []

  constructor(private service: ExplorerInputPort){}

  async ngOnChanges(changes: SimpleChanges) {
    if(this.generatedIdParams){
      var content:any = await this.service.getMounted();    
      this.mounted = this.traverse(content)
      var arr = this.generatedIdParams.filter((ele:any)=>{ 
        if(ele.type == 'file'){
          return ele  
        }
      })
      this.showFlag = (arr.length > 0)?true:false
    }
    console.log(this.generatedIdParams)
  }
  
  configure(item:any){
    this.items = item
    this.showExplorer = true
    setTimeout(()=>{
      $('#tree-explorer-'+this.items.key).jstree({'core' : {'data' : this.mounted}})
      .on('changed.jstree', function (e:any, data:any) {
        var path = data.instance.get_path(data.node,'/');  
        item.value = path
      })
    },10)
  }

  traverse(node:any) {
        let jstreeNode:any = {
            text: node.name,
            type: node.type,
        };
        if (node.type === 'folder') {
            jstreeNode.children = node.content.map((child:any) => this.traverse(child));
        }

        return jstreeNode;
  }

}  
