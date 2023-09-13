import { Component } from '@angular/core';

@Component({
  selector: 'app-ops-base',
  templateUrl: './ops-base.component.html',
  styleUrls: ['./ops-base.component.css']
})
export class OpsBaseComponent {
  goTo(section:string){
    var seccion = document.getElementById(section);
    seccion?.focus()
  }
}
