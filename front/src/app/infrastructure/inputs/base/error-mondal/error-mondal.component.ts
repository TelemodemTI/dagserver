import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-error-mondal',
  templateUrl: './error-mondal.component.html',
  styleUrls: ['./error-mondal.component.css']
})
export class ErrorMondalComponent {
  @Input("errorMsje") error_msje:any;
  @Input("titleMsje") title_msje:any;
  @Input("idModal") idModal:any;
}
