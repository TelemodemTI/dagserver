import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';

declare var $:any;
@Component({
  selector: 'app-props',
  templateUrl: './props.component.html',
  styleUrls: ['./props.component.css']
})
export class PropsComponent {

  @ViewChild("valuemodal") valuemodal!: ValueModalComponent;
  properties:any[] = []
  newvalue!:any
  oldv!:any
  group!:any
  constructor(private router: Router, 
    private service: PropsInputPort){}

  async ngOnInit() {
    this.properties = [];
    setTimeout(function () {
      $('#dataTables-jobs').DataTable({responsive: true});
    },100)
    this.properties = await this.service.properties();
  }
  async deleteProp(item:any){
    try {
      await this.service.deleteProperty(item.name,item.group)  
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"props"]);
      }); 
    } catch (error) {
      this.router.navigateByUrl("auth/props");
    }
  }
  async saveChanges(){
    var name = $("#namepropinput").val()
    var description = $("#descrpropinput").val()
    var value = $("#valuepropinput").val()
    var group = $("#grouppropinput").val()
    try {
      await this.service.createProperty(name,description,value,group);
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"props"]);
      });
    } catch (error) {
      localStorage.removeItem("dagserver_token");
      this.router.navigateByUrl("");
    }
  }
  viewProp(propval:any,i:any){
    $("#paramval-"+i).text(propval.value)
    setTimeout(()=>{
      $("#paramval-"+i).text("")
    },5000)
  }
  edit(propval:any,i:any){
    this.newvalue = propval.name
    this.oldv = propval.value
    this.group = propval.group
    this.valuemodal.show()
  }
  async deleteGroup(item:any){
    try {
      await this.service.deleteGroupProperty(item.name,item.group)  
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['auth',"props"]);
      }); 
    } catch (error) {
      this.router.navigateByUrl("auth/props");
    }
  }
  async changeValueEvent(event:any){
    await this.service.updateProp(this.group,event[0],event[1])
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"props"]);
    });
  }
}
