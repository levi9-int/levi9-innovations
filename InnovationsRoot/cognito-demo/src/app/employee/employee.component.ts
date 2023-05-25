import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';
import { Innovation } from '../models/innovation';
import { fullUserInfoResponse } from '../models/get-userid-response';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss']
})
export class EmployeeComponent implements OnInit{

  userDetailsList: fullUserInfoResponse = {} as fullUserInfoResponse;
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
        this.userDetailsList.name = user.attributes.given_name;
        this.userDetailsList.lastname = user.attributes.family_name;
        this.innovation.userId = user.username;
        this.fetchUsersInnovations();
      }
      else {
        this.router.navigate(['/sign-in']);
      }
    })
  }

  private fetchUsersInnovations() {
    this.employeeService.fetchUsersInnovations().subscribe({
      next: (res) => {
        this.employeeService.fullUserInfo = res;
        this.userDetailsList = res;
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
        this.fetchUsersInnovations();
        this.showSubmitPopup = false;
      },
      error: (err) => {
        console.log(err);
      }
    })

  }
}