import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddticketComponent } from './addticket.component';

describe('AddticketComponent', () => {
  let component: AddticketComponent;
  let fixture: ComponentFixture<AddticketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddticketComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddticketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
