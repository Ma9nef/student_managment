import { Component, OnInit } from '@angular/core';
import { DepartmentService } from '../../services/department';

import { DepartmentFormComponent } from './department-form/department-form.component';
import { DepartmentListComponent } from './department-list/department-list.component';

@Component({
  selector: 'app-department-page',
  standalone: true,
  imports: [DepartmentFormComponent, DepartmentListComponent],
  template: `
    <app-department-form (added)="reload()"></app-department-form>
    <hr />
    <app-department-list
      [departments]="departments"
      (deleted)="deleteDepartment($event)">
    </app-department-list>
  `
})
export class DepartmentPageComponent implements OnInit {

  departments: any[] = [];

  constructor(private service: DepartmentService) {}

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.service.getAll().subscribe((data: any[]) => {
      this.departments = data;
    });
  }

  deleteDepartment(id: number): void {
    this.service.delete(id).subscribe(() => {
      this.reload();
    });
  }
}
