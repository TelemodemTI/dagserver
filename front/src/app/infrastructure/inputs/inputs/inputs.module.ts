import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputsChannelsComponent } from './inputs-channels/inputs-channels.component';
import { BaseModule } from '../base/base.module';



@NgModule({
  declarations: [  
    InputsChannelsComponent
  ],
  imports: [
    CommonModule,
    BaseModule
  ],
  exports: [
    InputsChannelsComponent
  ]
})
export class InputsModule { }
