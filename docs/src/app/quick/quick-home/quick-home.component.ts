import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-quick-home',
  templateUrl: './quick-home.component.html',
  styleUrls: ['./quick-home.component.css']
})
export class QuickHomeComponent {
  goTo(section:string){
    var seccion = document.getElementById(section);
    seccion?.focus()
  }
}
