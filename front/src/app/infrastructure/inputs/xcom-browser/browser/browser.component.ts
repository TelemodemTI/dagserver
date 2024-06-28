import { Component, OnInit } from '@angular/core';
import { BrowserInputPort } from 'src/app/application/inputs/browser.input.port';
declare var $:any
declare var jQuery:any
@Component({
  selector: 'app-browser',
  templateUrl: './browser.component.html',
  styleUrls: ['./browser.component.css']
})
export class BrowserComponent implements OnInit {
  keys:any[] = []
  constructor(private service: BrowserInputPort){}
  async ngOnInit() {
    this.keys = await this.service.getXcomKeys();
    let root = this
    setTimeout(()=>{
      $('#jstree_demo_div').jstree();
      $('#jstree_demo_div').on('changed.jstree', function (e:EventTarget, data:any) {
        root.service.getEntry(data.node.text).then((data:any)=>{
          console.log(data)
        })
      })
    },100)
  }

}
