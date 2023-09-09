import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuickHomeComponent } from './quick-home.component';

describe('QuickHomeComponent', () => {
  let component: QuickHomeComponent;
  let fixture: ComponentFixture<QuickHomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ QuickHomeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuickHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
