import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';
import { Innovation } from '../models/innovation';
import { InnovationStatus } from 'app/enum/innovationstatus';

@Component({
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.scss']
})
export class EmployeeComponent implements OnInit{

  innovations: Innovation[] = [];
  innovation: Innovation = {} as Innovation;
  showRegistrationPopup: boolean = false;
  innovationStatus = Object.values(InnovationStatus);
  sucessfulAdding: boolean = false;



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
        console.log(user.id)
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
        this.innovations = res;
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

  showRegistrationForm() {
    console.log("aaaaaaaaaaaaaaaa")
    this.showRegistrationPopup = true;
  }

  closeRegistrationForm() {
    this.showRegistrationPopup = false;
  }

  saveInnovation(){
    console.log(this.innovation)
    this.employeeService.addInnovation(this.innovation).subscribe({
      next: (res) => {
        this.sucessfulAdding = true;
        console.log("aaaaaaaaaaaa");
      },
      error: (err) => {
        console.log(err);
      }
    })

  }
}


