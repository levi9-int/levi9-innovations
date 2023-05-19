
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';
import { Innovation } from '../models/innovation';
import { InnovationStatus } from 'app/enum/innovationstatus';

@Component({
  selector: 'app-lead',
  templateUrl: './lead.component.html',
  styleUrls: ['./lead.component.scss']
})
export class LeadComponent implements OnInit{


  showRegistrationPopup: boolean = false;
  innovations: Innovation[] = []; 
  selectedInnovation: Innovation | undefined;

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
        console.log(user.id)
        this.fetchPendingInnovations();
      }
      else {
        this.router.navigate(['/sign-in']);
      }
    })
  }

  private fetchPendingInnovations() {
    this.employeeService.fetchPendingInnovations().subscribe({
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
    console.log("aaaaaaaaaaaaaaaaaaa");
    this.showRegistrationPopup = false;
  }

  selectInnovation(innovation: Innovation) {
    this.selectedInnovation = innovation;
    // Otvorite formu ili izvršite druge akcije koje su vam potrebne
  }

  openForm(innovation: Innovation) {
    // Ovde možete izvršiti dodatne akcije, na primer, popuniti formu sa podacima iz inovacije
    this.showRegistrationPopup = true;
  }


}
