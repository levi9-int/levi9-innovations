import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { Innovation } from 'app/models/innovation';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly API_PATH = environment.apiPath;

  constructor(private http: HttpClient) { }

  fetchUsersInnovations(userId: string): Observable<Innovation[]> {
    return this.http.get<Innovation[]>(this.API_PATH + 'get-innovation?userId=' + userId);
  }

  fetchPendingInnovations(): Observable<Innovation[]> {
    return this.http.get<Innovation[]>(this.API_PATH + 'get-innovation?status=PENDING');}

    addInnovation(innovation: Innovation): Observable<any> {
    return this.http.post<any>(this.API_PATH + 'add-innovation', innovation);
  }

  

}
