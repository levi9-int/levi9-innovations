import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeadComponent } from './lead.component';

describe('LeadComponent', () => {
  let component: LeadComponent;
  let fixture: ComponentFixture<LeadComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LeadComponent]
    });
    fixture = TestBed.createComponent(LeadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
