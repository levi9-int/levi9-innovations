import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'app/models/user';
import { CognitoService } from 'app/service/cognito.service';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements OnInit{

  user: User | undefined;
  alertMessage: string = '';
  showAlert: boolean = false;
  
  isForgotPassword: boolean = false;
  newPassword: string = '';

  constructor(private router: Router, private cognitoService: CognitoService) {}


  ngOnInit(): void {
    this.user = {} as User;
  }

  signInWithCognito() {
    if (this.user && this.user.email && this.user.password) {
      this.cognitoService.signIn(this.user)
      .then((res) => {
        console.log(res.signInUserSession);
        this.cognitoService.setAccessToken(res.signInUserSession.idToken);
        // todo: check if logged in user is employee or lead
        this.router.navigate(['/employee'])
      })
      .catch((error: any) => {
        this.displayAlert(error.message);
      })
    }
  }

  private displayAlert(message:string) {
    this.alertMessage = message;
    this.showAlert = true;
  }

  forgotPasswordClicked() {
    if (this.user && this.user.email) {
      this.cognitoService.forgotPassword(this.user)
      .then(() => {
        this.isForgotPassword = true;
      })
      .catch((error:any) => {
        this.displayAlert(error.message);
      })
    }
    else {
      this.displayAlert("Please Enter a valid email address");
    }
  }

  newPasswordSubmit() {
    if (this.user && this.user.code && this.newPassword.trim().length != 0) {
      this.cognitoService.forgotPasswordSubmit(this.user, this.newPassword.trim())
      .then(() => {
        this.displayAlert("Password Updated");
        this.isForgotPassword = false;
      })
      .catch((error:any) => {
        this.displayAlert(error.message);
      })
    }
    else {
      this.displayAlert("Please enter valid input");
    }
  }

}