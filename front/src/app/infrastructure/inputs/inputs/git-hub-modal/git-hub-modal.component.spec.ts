import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GitHubModalComponent } from './git-hub-modal.component';

describe('GitHubModalComponent', () => {
  let component: GitHubModalComponent;
  let fixture: ComponentFixture<GitHubModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GitHubModalComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GitHubModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
