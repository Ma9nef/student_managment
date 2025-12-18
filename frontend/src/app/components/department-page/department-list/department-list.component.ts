import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-department-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './department-list.component.html'
})
export class DepartmentListComponent {

  @Input() departments: any[] = [];

  @Output() deleted = new EventEmitter<number>();

  onDelete(id: number): void {
    this.deleted.emit(id);
  }
}
