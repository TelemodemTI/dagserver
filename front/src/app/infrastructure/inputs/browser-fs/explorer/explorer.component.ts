import { Component, OnInit, ViewChild } from '@angular/core';
import { ExplorerInputPort } from 'src/app/application/inputs/explorer.input.port';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';
import { Router } from '@angular/router';
import { UploadModalComponent } from '../../base/upload-modal/upload-modal.component';
declare var $:any
@Component({
  selector: 'app-explorer',
  templateUrl: './explorer.component.html',
  styleUrls: ['./explorer.component.css']
})
export class ExplorerComponent implements OnInit{
@ViewChild("valuer") valuer!:ValueModalComponent
@ViewChild("valuer2") valuer2!:ValueModalComponent
@ViewChild("valuer3") valuer3!:ValueModalComponent

@ViewChild("uploader") uploader!:UploadModalComponent;
selected_folder:string = "/";
selected_file:string = "";
constructor(private service: ExplorerInputPort, private router: Router){}

  ngOnInit(): void {
    setTimeout(()=>{
      this.initBrowser();
    },100) 
  }
  async initBrowser() {
    var root = this
    var content:any = await this.service.getMounted();    
    const pluginFormat = this.transformApiResponseToPluginFormat(content);
    $("#files").simpleFileBrowser({
      json: pluginFormat,
      path: root.selected_folder,
      view: 'icon',
      select: true,
      breadcrumbs: true,
      onSelect: function (obj:any, file:any, folder:any, type:any) {
        if(type == "folder"){
          root.selected_folder  = file
          root.selected_file = ""
        } else {
          root.selected_folder  = folder
          root.selected_file = file
        }
      },
      onOpen: function (obj:any,file:any, folder:any, type:any) {
        if(type == "folder"){
          root.selected_folder  = file
          root.selected_file = ""
        } else {
          root.selected_folder  = folder
          root.selected_file = file
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
  showUpload(){
    this.uploader.show();
  }
  createCopy(){
    this.valuer2.show();
  }
  move(){
    this.valuer3.show();
  }
  createFolder(){
    this.valuer.show();
  }
  async uploadEvent(file:any){
    await this.service.uploadMounted(file,this.selected_folder)
    this.uploader.close();
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
  async changeValueEvent(arr:any){
    let folder = arr[1];
    await this.service.createFolder(folder)
    this.valuer.close();
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
  async changeValueCopyEvent(arr:any){
    let filename = arr[1];
    await this.service.createCopy(this.selected_file, filename)
    this.valuer.close();
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
  async changeValueMoveEvent(arr:any){
    let newpath = arr[1];
    await this.service.move(this.selected_folder,this.selected_file,newpath)
    this.valuer.close();
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
  goHome(){
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
  async delete(){
    await this.service.delete(this.selected_folder,this.selected_file)
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
  async download(){
    if(this.selected_file) {
      await this.service.download(this.selected_folder,this.selected_file)
    }
  }
}
