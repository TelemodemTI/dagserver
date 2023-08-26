import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { InputsChannelsInputPort } from 'src/app/application/inputs/inputschannels.input.port';

@Component({
  selector: 'app-inputs-channels',
  templateUrl: './inputs-channels.component.html',
  styleUrls: ['./inputs-channels.component.css']
})
export class InputsChannelsComponent {

  items:any[] = []

  constructor(private router: Router, 
    private service: InputsChannelsInputPort){
  }

  ngOnInit() {
    this.items = []
    this.service.getChannels().then((data)=>{
      console.log(data)
      this.items = data      
    })
  }
  options(item:any){

  }
}
