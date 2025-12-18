import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentService } from '../../../services/department';

@Component({
  selector: 'app-department-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './department-form.component.html'
})
export class DepartmentFormComponent {

  @Output() added = new EventEmitter<void>();

  department = {
    name: '',
    location: '',
    phone: '',
    head: ''
  };

  constructor(private service: DepartmentService) {}

  add() {
    this.service.create(this.department).subscribe(() => {
      this.department = { name: '', location: '', phone: '', head: '' };
      this.added.emit(); // 🔥 recharge uniquement après ajout
    });
  }
}
