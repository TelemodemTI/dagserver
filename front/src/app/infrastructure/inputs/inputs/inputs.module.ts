import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputsChannelsComponent } from './inputs-channels/inputs-channels.component';
import { BaseModule } from '../base/base.module';
import { RabbitModalComponent } from './rabbit-modal/rabbit-modal.component';
import { RedisModalComponent } from './redis-modal/redis-modal.component';
import { KafkaModalComponent } from './kafka-modal/kafka-modal.component';
import { ActivemqModalComponent } from './activemq-modal/activemq-modal.component';
import { HttpApiModalComponent } from './http-api-modal/http-api-modal.component';



@NgModule({
  declarations: [  
    InputsChannelsComponent, RabbitModalComponent, RedisModalComponent, KafkaModalComponent, ActivemqModalComponent, HttpApiModalComponent
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
