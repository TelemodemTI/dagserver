import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocBaseComponent } from './doc-base.component';

describe('DocBaseComponent', () => {
  let component: DocBaseComponent;
  let fixture: ComponentFixture<DocBaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocBaseComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocBaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
