import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FindTicketsAssignedToUserComponent } from './find-tickets-assigned-to-user.component';

describe('FindTicketsAssignedToUserComponent', () => {
  let component: FindTicketsAssignedToUserComponent;
  let fixture: ComponentFixture<FindTicketsAssignedToUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FindTicketsAssignedToUserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FindTicketsAssignedToUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
