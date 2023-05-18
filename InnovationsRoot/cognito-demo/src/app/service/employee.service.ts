import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly API_PATH = environment.apiPath;

  constructor(private http: HttpClient) { }

  fetchUsersInnovations(userId: string): Observable<any> {
    return this.http.get<any>(this.API_PATH + 'get-innovation?userId=' + userId);
  }

}
