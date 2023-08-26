import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InputsChannelsComponent } from './inputs-channels.component';

describe('InputsChannelsComponent', () => {
  let component: InputsChannelsComponent;
  let fixture: ComponentFixture<InputsChannelsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InputsChannelsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InputsChannelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
