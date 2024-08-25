import { Component, OnInit } from '@angular/core';
import { ExplorerInputPort } from 'src/app/application/inputs/explorer.input.port';
declare var $:any
@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.css']
})
export class ExplorerComponent implements OnInit{
  
constructor(private service: ExplorerInputPort){}

  ngOnInit(): void {
    setTimeout(()=>{
      this.initBrowser();
    },100)
    
  }
  async initBrowser() {
    var content:any = await this.service.getMounted();
    
    console.log(content)
    console.log(JSON.stringify(content))
    
    const pluginFormat = this.transformApiResponseToPluginFormat(content);

    console.log(pluginFormat)

    $("#files").simpleFileBrowser({
      json: pluginFormat,
      path: '/',
      view: 'icon',
      select: false,
      breadcrumbs: true,
      onSelect: function (obj:any, file:any, folder:any, type:any) {
          $("#select").html("You select a "+type+" "+folder+'/'+file);
      },
      onOpen: function (obj:any,file:any, folder:any, type:any) {
          if (type=='file') {
              alert("Open file: "+folder+'/'+file);
          }
      }
    });
  }

 transformApiResponseToPluginFormat(entry:any, path = '') {
    let result :any= {};
    const currentPath = path + entry.name;
  
    result[currentPath] = entry.content.map((item:any) => ({
      name: item.name,
      type: item.type
    }));
  
    entry.content.forEach((item:any) => {
      if (item.type === 'folder') {
        Object.assign(result, this.transformApiResponseToPluginFormat(item, currentPath));
      }
    });
  
    return result;
  }
}
