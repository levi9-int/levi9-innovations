import { Injectable } from '@angular/core';
import { Amplify, Auth } from 'aws-amplify';
import { User } from '../models/user';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class CognitoService {


  constructor() {
    Amplify.configure({
      Auth:environment.cognito
    })
  }

   setAccessToken(accessToken: any) {
    sessionStorage.setItem("jwt", accessToken.jwtToken);
  }

  getToken() {
    return sessionStorage.getItem("jwt");
  }


  public signUp(user: User): Promise<any> {
    return Auth.signUp({
      username: user.email,
      password: user.password,
      attributes: {
        email: user.email,
        given_name: user.givenName,
        family_name: user.familyName
      }
    })
  }

  public confirmSignUp(user: User) : Promise<any> {
    return Auth.confirmSignUp(user.email, user.code);
  }

  public changeLeadPassword(user: User, password: string) : Promise<any> {
    return Auth.completeNewPassword(user, password);
  }

  // this method will return user info if any user
  // is logged in with valid email and password
  public getUser(): Promise<any> {
    return Auth.currentUserInfo();
  }

  public signIn(user: User): Promise<any> {
    return Auth.signIn(user.email, user.password);
  }

  public signOut() : Promise<any> {
    sessionStorage.removeItem("jwt");
    return Auth.signOut();
  }

  public forgotPassword(user: User): Promise<any> {
    return Auth.forgotPassword(user.email);
  }

  public forgotPasswordSubmit(user: User, new_password: string) : Promise <any> {
    return Auth.forgotPasswordSubmit(user.email, user.code, new_password);
  }
}
