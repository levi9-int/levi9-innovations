import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';
import { Innovation } from '../models/innovation';
import { GetUserIdResponse } from '../models/get-userid-response';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss']
})
export class EmployeeComponent implements OnInit{

  getResponse: GetUserIdResponse = {} as GetUserIdResponse;
  innovation: Innovation = {} as Innovation;
  showSubmitPopup: boolean = false;

  constructor(private router: Router, private cognitoService: CognitoService,
    private employeeService: EmployeeService,
   ) {

   }

  ngOnInit(): void {
    this.getUserDetails();
  }


  private getUserDetails() {
    this.cognitoService.getUser()
    .then((user:any) => {
      if (user) {
        this.innovation.userId = user.username;
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
        this.getResponse = res;
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

  showSubmitForm() {
    this.showSubmitPopup = true;
  }

  closeSubmitForm() {
    this.showSubmitPopup = false;
  }

  saveInnovation(){
    console.log(this.innovation)
    this.employeeService.addInnovation(this.innovation).subscribe({
      next: (res) => {
        this.fetchUsersInnovations(this.innovation.userId);
        this.showSubmitPopup = false;
      },
      error: (err) => {
        console.log(err);
      }
    })

  }
}