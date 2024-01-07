import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KafkaModalComponent } from './kafka-modal.component';

describe('KafkaModalComponent', () => {
  let component: KafkaModalComponent;
  let fixture: ComponentFixture<KafkaModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KafkaModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KafkaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
