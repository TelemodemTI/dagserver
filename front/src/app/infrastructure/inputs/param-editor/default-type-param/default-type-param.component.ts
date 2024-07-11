import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-default-type-param',
  templateUrl: './default-type-param.component.html',
  styleUrls: ['./default-type-param.component.css']
})
export class DefaultTypeParamComponent {
  @Input("generatedIdParams") generatedIdParams:any
  @Input("xcoms") xcoms:any[] = []
  
}
