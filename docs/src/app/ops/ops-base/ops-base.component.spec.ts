import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OpsBaseComponent } from './ops-base.component';

describe('OpsBaseComponent', () => {
  let component: OpsBaseComponent;
  let fixture: ComponentFixture<OpsBaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OpsBaseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OpsBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
