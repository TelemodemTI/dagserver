import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RabbitModalComponent } from './rabbit-modal.component';

describe('RabbitModalComponent', () => {
  let component: RabbitModalComponent;
  let fixture: ComponentFixture<RabbitModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RabbitModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RabbitModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
