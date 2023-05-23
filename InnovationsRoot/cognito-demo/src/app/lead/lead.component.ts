
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CognitoService } from '../service/cognito.service';
import { EmployeeService } from '../service/employee.service';
import { Innovation } from '../models/innovation';
import { ReviewRequest } from 'app/models/review-request';
import { User } from 'app/models/user';
import { InnovationUserDetailsResponse } from 'app/models/innovation-detail-response';

@Component({
  selector: 'app-lead',
  templateUrl: './lead.component.html',
  styleUrls: ['./lead.component.scss']
})
export class LeadComponent implements OnInit{


  showReviewPopup: boolean = false;
  innovationUserList: InnovationUserDetailsResponse[] = []; 
  selectedInnovation: Innovation = {} as Innovation;
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
        this.innovationUserList = res;
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

  showReviewForm() {
    this.showReviewPopup = true;
  }

  closeReviewForm() {
    this.showReviewPopup = false;
  }

  selectInnovation(innovation: Innovation) {
    this.selectedInnovation = innovation;
    this.showReviewPopup = true;
    // Otvorite formu ili izvrÅ¡ite druge akcije koje su vam potrebne
  }

  reviewInnovation(approved: boolean){
    let reviewRequst: ReviewRequest = {} as ReviewRequest;
    reviewRequst.title = this.selectedInnovation.title;
    reviewRequst.description = this.selectedInnovation.description;
    reviewRequst.comment = this.selectedInnovation.comment;
    reviewRequst.innovationId = this.selectedInnovation.id;
    reviewRequst.userId = this.selectedInnovation.userId;
    reviewRequst.approved = approved;
    this.employeeService.reviewInnovations(reviewRequst).subscribe({
      next: (res) => {
        this.fetchPendingInnovations();
        this.closeReviewForm();
      },
      error: (err) => {
        console.log(err);
      }
    })
  }



}