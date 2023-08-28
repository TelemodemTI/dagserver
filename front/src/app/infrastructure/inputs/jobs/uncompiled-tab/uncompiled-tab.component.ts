import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
import { Uncompileds } from 'src/app/domain/models/uncompiled.model';
declare var $:any;
@Component({
  selector: 'app-uncompiled-tab',
  templateUrl: './uncompiled-tab.component.html',
  styleUrls: ['./uncompiled-tab.component.css']
})
export class UncompiledTabComponent {

  uncompileds:Uncompileds[] = []
  title_msje:any = "Error"
  error_msje:any = ""
  
  constructor(private router: Router, 
    private service: JobsInputPort){
  }

  async ngOnInit(): Promise<void> {
    setTimeout(()=>{
      $('#dataTables-uncompiledjobs').DataTable({ responsive: true });
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
}
