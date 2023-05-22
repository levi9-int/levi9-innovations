
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';
import { Innovation } from '../models/innovation';
import { InnovationStatus } from 'app/enum/innovationstatus';
import { ReviewRequest } from 'app/models/review-request';
import { User } from 'app/models/user';

@Component({
  selector: 'app-lead',
  templateUrl: './lead.component.html',
  styleUrls: ['./lead.component.scss']
})
export class LeadComponent implements OnInit{


  showRegistrationPopup: boolean = false;
  innovations: Innovation[] = []; 
  selectedInnovation: Innovation = {} as Innovation;
  selectedUserId: string | undefined;
  selecetedInnovationId: string | undefined;
  comment : string = "";
  user: User = {} as User;

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
        console.log(user);
        console.log("ASADSFBSFB "+user.attributes.given_name);
        this.user.givenName = user.attributes.given_name;
        this.user.familyName = user.attributes.family_name;
        //this.user = user;
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
    
    this.showRegistrationPopup = false;
    console.log("zatvori se smore");
    
  }

  selectInnovation(innovation: Innovation) {
    this.selectedInnovation = innovation;
    this.showRegistrationPopup = true;
    // Otvorite formu ili izvršite druge akcije koje su vam potrebne
  }

  openForm(userId: string, innovationId:string) {
    this.selecetedInnovationId = innovationId;
    console.log(innovationId);
    this.selectedUserId = userId;
    console.log(userId);
    this.showRegistrationPopup = true;
  }

  reviewInnovation(approved: boolean){
    let reviewRequst: ReviewRequest = {} as ReviewRequest;
    reviewRequst.title = this.selectedInnovation.title;
    reviewRequst.description = this.selectedInnovation.description;
    reviewRequst.comment = this.selectedInnovation.comment;
    reviewRequst.innovationId = this.selectedInnovation.id;
    reviewRequst.userId = this.selectedInnovation.userId;
    this.employeeService.reviewInnovations(reviewRequst).subscribe({
      next: (res) => {
        this.closeRegistrationForm();
        console.log(res);
      },
      error: (err) => {
        console.log(err);
      }
    })
  }



}
