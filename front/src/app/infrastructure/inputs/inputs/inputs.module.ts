import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputsChannelsComponent } from './inputs-channels/inputs-channels.component';
import { BaseModule } from '../base/base.module';
import { GitHubModalComponent } from './git-hub-modal/git-hub-modal.component';
import { RabbitModalComponent } from './rabbit-modal/rabbit-modal.component';
import { RedisModalComponent } from './redis-modal/redis-modal.component';
import { KafkaModalComponent } from './kafka-modal/kafka-modal.component';
import { ActivemqModalComponent } from './activemq-modal/activemq-modal.component';



@NgModule({
  declarations: [  
    InputsChannelsComponent, GitHubModalComponent, RabbitModalComponent, RedisModalComponent, KafkaModalComponent, ActivemqModalComponent
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
