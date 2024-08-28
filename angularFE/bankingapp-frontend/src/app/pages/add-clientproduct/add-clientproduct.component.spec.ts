import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddClientproductComponent } from './add-clientproduct.component';

describe('AddClientproductComponent', () => {
  let component: AddClientproductComponent;
  let fixture: ComponentFixture<AddClientproductComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddClientproductComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddClientproductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
