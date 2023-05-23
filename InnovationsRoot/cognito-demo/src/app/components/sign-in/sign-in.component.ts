import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { CognitoService } from 'src/app/service/cognito.service';

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
      .then((cogUser) => {
        console.log(cogUser);
        if (!cogUser.signInUserSession) {
          this.cognitoService.changeLeadPassword(cogUser, 'JanaLead123')
          .then((res) => {
            this.successSignIn(res);
          })
        }
        else {
          this.successSignIn(cogUser);
        }
      })
      .catch((error: any) => {
        this.displayAlert(error.message);
      })
    }
  }

  
  successSignIn(cogUser: any) {
    this.cognitoService.setAccessToken(cogUser.signInUserSession.idToken);
    if (cogUser.signInUserSession.idToken.payload['cognito:groups'].includes('EmployeeGroup'))
      this.router.navigate(['/employee'])
    else 
      this.router.navigate(['/lead'])
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

