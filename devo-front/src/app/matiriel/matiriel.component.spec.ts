import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatirielComponent } from './matiriel.component';

describe('MatirielComponent', () => {
  let component: MatirielComponent;
  let fixture: ComponentFixture<MatirielComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatirielComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MatirielComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
