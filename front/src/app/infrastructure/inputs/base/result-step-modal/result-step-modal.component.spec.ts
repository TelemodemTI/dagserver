import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultStepModalComponent } from './result-step-modal.component';

describe('ResultStepModalComponent', () => {
  let component: ResultStepModalComponent;
  let fixture: ComponentFixture<ResultStepModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResultStepModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResultStepModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
