import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private baseUrl = 'http://localhost:8080/auth/'; // aws cognito endpoint?

  constructor(private router: Router, private http: HttpClient) { }

  login(loginInfo: any) {
    return this.http.post<any>(`${this.baseUrl}login`, loginInfo)
      .pipe(map((res) => {
        sessionStorage.setItem("jwt", res.accessToken)
      }));
  }

  logout() {
    this.http.get<any>(`${this.baseUrl}logout`).subscribe()
    sessionStorage.removeItem("jwt");
    this.router.navigate(['/login']);
  }

  getToken() {
    return sessionStorage.getItem("jwt"); //this.access_token;
  }

  navigateForRole() {
    const token = this.getToken()
    if (token == null)
      return;
    const jwt: JwtHelperService = new JwtHelperService()
    const info = jwt.decodeToken(token)

    if (info.role === "ROLE_LEAD")
      this.router.navigate(['/lead/home']);
    else
      this.router.navigate(['/employee/home'])
  }

}
