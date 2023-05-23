import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { GetUserIdResponse } from '../models/get-userid-response';
import { Innovation } from '../models/innovation';
import { InnovationUserDetailsResponse } from '../models/innovation-detail-response';
import { ReviewRequest } from '../models/review-request';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly API_PATH = environment.apiPath;

  constructor(private http: HttpClient) { }

  fetchUsersInnovations(userId: string): Observable<GetUserIdResponse> {
    return this.http.get<GetUserIdResponse>(this.API_PATH + 'get-innovation?userId=' + userId);
  }

  fetchPendingInnovations(): Observable<InnovationUserDetailsResponse[]> {
    return this.http.get<InnovationUserDetailsResponse[]>(this.API_PATH + 'get-innovation?status=PENDING');}

    addInnovation(innovation: Innovation): Observable<any> {
    return this.http.post<any>(this.API_PATH + 'add-innovation', innovation);
  }

  reviewInnovations(innovation:ReviewRequest): Observable<any> {
    console.log(innovation);
    return this.http.put<any>(this.API_PATH + 'review-innovation', innovation);
  }

  

}
