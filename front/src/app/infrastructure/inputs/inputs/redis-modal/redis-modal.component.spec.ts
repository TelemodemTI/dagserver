import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RedisModalComponent } from './redis-modal.component';

describe('RedisModalComponent', () => {
  let component: RedisModalComponent;
  let fixture: ComponentFixture<RedisModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RedisModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RedisModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
