import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepartmentPage } from './department-page.component';

describe('DepartmentPage', () => {
  let component: DepartmentPage;
  let fixture: ComponentFixture<DepartmentPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartmentPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DepartmentPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
