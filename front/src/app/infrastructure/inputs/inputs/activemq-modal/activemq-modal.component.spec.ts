import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivemqModalComponent } from './activemq-modal.component';

describe('ActivemqModalComponent', () => {
  let component: ActivemqModalComponent;
  let fixture: ComponentFixture<ActivemqModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActivemqModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivemqModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
