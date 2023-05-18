import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse, HttpHeaders,
} from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private router: Router, private authService: CognitoService) { }

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {

    const authToken = this.authService.getToken();
    if (authToken) {
      const header = new HttpHeaders({ Authorization: 'Bearer ' + authToken});
      
      const cloned = req.clone({
        headers: header,
        withCredentials: true,
      });

      return next.handle(cloned).pipe(
        tap(
          (err) => {
            if (err instanceof HttpErrorResponse) {
              if (err.status !== 401) {
                return;
              }
              // this.authService.logout();
            }
          }
        )
      );
    } else {
      const cloned = req.clone({
        withCredentials: true,
      })
      return next.handle(cloned);
    }
  }
}