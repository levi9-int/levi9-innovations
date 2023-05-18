import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss']
})
export class EmployeeComponent implements OnInit{

  constructor(private router: Router, private cognitoService: CognitoService,
    private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.getUserDetails();
  }


  private getUserDetails() {
    this.cognitoService.getUser()
    .then((user:any) => {
      if (user) {
        this.fetchUsersInnovations(user.username);
      }
      else {
        this.router.navigate(['/sign-in']);
      }
    })
  }

  private fetchUsersInnovations(username: string) {
    this.employeeService.fetchUsersInnovations(username).subscribe({
      next: (res) => {
        console.log(res);
      },
      error: (err) => {
        console.log(err);
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


