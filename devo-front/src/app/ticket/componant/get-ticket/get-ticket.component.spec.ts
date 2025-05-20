import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetTicketComponent } from './get-ticket.component';

describe('GetTicketComponent', () => {
  let component: GetTicketComponent;
  let fixture: ComponentFixture<GetTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetTicketComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
