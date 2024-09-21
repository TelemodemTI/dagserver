import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { KeystoreInputPort } from 'src/app/application/inputs/keystore.input.port';

@Component({
  selector: 'app-default-type-param',
  templateUrl: './default-type-param.component.html',
  styleUrls: ['./default-type-param.component.css']
})
export class DefaultTypeParamComponent implements OnChanges {

  constructor(private service: KeystoreInputPort){}
  
  @Input("generatedIdParams") generatedIdParams:any
  @Input("xcoms") xcoms:any[] = []
  entries:any[] = []
  
  async ngOnChanges(changes: SimpleChanges) {
    console.log(this.generatedIdParams)
    this.entries = await this.service.getEntries()
  }
}
