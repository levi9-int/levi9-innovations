import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss']
})
export class EmployeeComponent implements OnInit{

  constructor(private router: Router, private cognitoService: CognitoService) {}

  ngOnInit(): void {
    this.getUserDetails();
  }


  private getUserDetails() {
    this.cognitoService.getUser()
    .then((user:any) => {
      if (user) {
        // logged in
        console.log(user);
      }
      else {
        this.router.navigate(['/sign-in']);
      }
    })
  }

  signOutWithCognito() {
    this.cognitoService.signOut()
    .then(() => {
      this.router.navigate(['/sign-in'])
    })
  }
}
