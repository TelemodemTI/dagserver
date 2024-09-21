import { ChangeDetectorRef, Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import {Buffer} from 'buffer';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
import { NewJInputPort } from 'src/app/application/inputs/mewj.input.port';
import { Uncompileds } from 'src/app/domain/models/uncompiled.model';
import { UploadModalComponent } from '../../base/upload-modal/upload-modal.component';
declare var $:any;
@Component({
  selector: 'app-uncompiled-tab',
  templateUrl: './uncompiled-tab.component.html',
  styleUrls: ['./uncompiled-tab.component.css']
})
export class UncompiledTabComponent implements OnInit, OnChanges {

  @ViewChild("propImporter") propImporter!: ElementRef;
  @ViewChild("uploader") uploader!: UploadModalComponent;
  uncompileds:Uncompileds[] = []
  title_msje:any = "Error"
  error_msje:any = ""
  uploadedBin:any = ""
  table!:any
  constructor(private router: Router, 
    private newjservice: NewJInputPort,
    private service: JobsInputPort,
    private cd: ChangeDetectorRef){
  }
  async ngOnChanges(changes: SimpleChanges) {
    this.cd.detectChanges();
    this.uncompileds = await this.service.getUncompileds();
  }
  
  async ngOnInit(): Promise<void> {
    setTimeout(()=>{
      this.table = $('#dataTables-uncompiledjobs').DataTable({ responsive: true });
    },500)
    this.uncompileds = await this.service.getUncompileds();
  }

  
  editUncompiled(item:any){
    this.router.navigateByUrl(`auth/njob/${item}`);
  }
  async compile(uncompiledId:number){
    try {
      let result = await this.service.compile(uncompiledId)
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"jobs"]);
      });  
    } catch (error:any) {   
      this.title_msje = error.status
      this.error_msje = error.message
      setTimeout(()=>{
        $('#errorUncompiled').modal('show');
      },100)
      
    }
  }
  async delete(uncompiledId:number){
    await this.service.deleteUncompiled(uncompiledId);
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"jobs"]);
    });   
  }
  async export(uncompiledId:number,filename:string){
    var bin:any = await this.service.exportUncompiled(uncompiledId);
    console.log(bin)
    const blob = new Blob([bin], { type: 'text/json' });
    const url= window.URL.createObjectURL(blob);
    // Crear un elemento 'a' para el enlace de descarga
    const a = document.createElement('a');
    a.href = url;
    a.download = filename+"_uncompiled.json";
    // Hacer clic en el enlace de descarga de forma programática
    a.click();
    // Liberar el objeto URL después de la descarga
    window.URL.revokeObjectURL(url);
  }
  selectEvent(event_req:any){
    const file = event_req.target.files[0]
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
        let atr :any = reader.result!
        let contentbase64 = atr.split(',')[1];
        this.uploadedBin = JSON.parse( atob(contentbase64) )
    };
  }
  async uploadEvent(file: any) {
    var base64 = Buffer.from(JSON.stringify(this.uploadedBin)).toString('base64')
    await this.newjservice.createUncompiled(base64) 
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"jobs"]);
    });  
  }
  import(){
    this.uploader.show();
  }
  async refresh(){
	  this.uncompileds = await this.service.getUncompileds();
  }
}
