import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ExplorerInputPort } from 'src/app/application/inputs/explorer.input.port';
declare var $:any;
@Component({
  selector: 'app-upload-file',
  templateUrl: './upload-file.component.html',
  styleUrls: ['./upload-file.component.css']
})
export class UploadFileComponent {

  file_content!:any
  event_req!:any
  upload_path!:string

  constructor(private service: ExplorerInputPort, private router:Router){}

  show(uploadPath:string) {
    this.upload_path = uploadPath
    $("#explorerUploadModal").modal("show")
  }

  upload(event: any) {
    this.event_req = event
  }
  async uploadMounted(){
    let file = this.event_req.target.files[0]
    let response = await this.service.uploadMounted(file,this.upload_path)
    $("#explorerUploadModal").modal("hide")
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"browser"]);
    });   
  }
}
