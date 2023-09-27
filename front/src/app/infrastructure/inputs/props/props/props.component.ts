import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { PropsInputPort } from 'src/app/application/inputs/props.input.port';
import { ValueModalComponent } from '../../base/value-modal/value-modal.component';
import { DomSanitizer } from '@angular/platform-browser';

declare var $:any;
@Component({
  selector: 'app-props',
  templateUrl: './props.component.html',
  styleUrls: ['./props.component.css']
})
export class PropsComponent {

  @ViewChild("valuemodal") valuemodal!: ValueModalComponent;
  @ViewChild("propImporter") propImporter!: ElementRef;
  

  propertiesOriginal:any[] = []
  properties:any[] = []
  newvalue!:any
  oldv!:any
  group!:any
  downloadJsonHref!:any
  downloadText!:any
  intervaledSearch:any
  uploadedProps!:any
  constructor(private router: Router, 
    private sanitizer: DomSanitizer,
    private service: PropsInputPort){}

  async ngOnInit() {
    this.properties = [];
    let root = this
    setTimeout(function () {
      var table = $('#dataTables-props').DataTable({responsive: true});
      table.on('search.dt',  (e:any, settings:any)=> {
        if(root.intervaledSearch)
          clearTimeout(root.intervaledSearch)
          root.intervaledSearch = setTimeout(()=>{
            var data = table.rows({  'search' : 'applied'  }).data();
            root.search(data)
          },1500)
      });
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
  search(data:any){
    let newprops = []
    for (let index = 0; index < data.length; index++) {
      const element = data[index];
      let nprop = this.properties.filter((ele:any)=> {return ele.group == element[0] && ele.name == element[1]})[0]
      newprops.push(nprop)
    }
    this.propertiesOriginal = this.properties
    this.properties = newprops
  }
  exportProps(){
    this.downloadText = this.properties.length + " Properties Selected"
    var theJSON = JSON.stringify(this.properties);
    var uri = this.sanitizer.bypassSecurityTrustUrl("data:text/json;charset=UTF-8," + encodeURIComponent(theJSON));
    this.downloadJsonHref = uri;
    setTimeout(()=>{
      this.downloadText = "";
    },5000)
  }
  importProps(){
    $("#importPropertiesModal").modal("show");
  }
  upload(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
        let atr :any = reader.result!
        let contentbase64 = atr.split(',')[1];
        this.uploadedProps = JSON.parse( atob(contentbase64))
    };
  }
  
  async importPropperties(){
    for (let index = 0; index < this.uploadedProps.length; index++) {
      const element = this.uploadedProps[index];
      await this.service.createProperty(element.name,element.description,element.value,element.group);
    }
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['auth',"props"]);
    });
  }
}
