import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {

private baseUrl = 'http://192.168.49.2:30080/Department';

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/getAllDepartment`);
  }

  create(dept: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/createDepartment`, dept);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/deleteDepartment/${id}`);
  }
}
