import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompiledTabComponent } from './compiled-tab.component';

describe('CompiledTabComponent', () => {
  let component: CompiledTabComponent;
  let fixture: ComponentFixture<CompiledTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompiledTabComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompiledTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
