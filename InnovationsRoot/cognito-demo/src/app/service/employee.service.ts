import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.development';
import { fullUserInfoResponse } from '../models/get-userid-response';
import { Innovation } from '../models/innovation';
import { InnovationUserDetailsResponse } from '../models/innovation-detail-response';
import { ReviewRequest } from '../models/review-request';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly API_PATH = environment.apiPath;

  private _fullUserInfo: fullUserInfoResponse = {} as fullUserInfoResponse;
  
  public get fullUserInfo(): fullUserInfoResponse {
    return this._fullUserInfo;
  }
  public set fullUserInfo(value: fullUserInfoResponse) {
    this._fullUserInfo = value;
  }

  constructor(private http: HttpClient) { }

  fetchUsersInnovations(userId: string): Observable<fullUserInfoResponse> {
    return this.http.get<fullUserInfoResponse>(this.API_PATH + 'get-innovation');;
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
