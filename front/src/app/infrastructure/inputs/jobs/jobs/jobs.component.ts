import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { JobsInputPort } from 'src/app/application/inputs/jobs.input.port';
declare var $:any;
@Component({
  selector: 'app-jobs',
  templateUrl: './jobs.component.html',
  styleUrls: ['./jobs.component.css']
})
export class JobsComponent {

  constructor(private router: Router, 
    private service: JobsInputPort,
    private cd: ChangeDetectorRef
  ){
  }
  

  title_msje:any = "Error"
  error_msje:any = ""  
  
  async ngOnInit(): Promise<void> {
  
  }
  
  
  createNew(){
    this.router.navigateByUrl(`auth/njob`);
  }
  
  
  
  
}
