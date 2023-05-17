import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../shared/components/login/login.component';
import { EmployeeRegistrationComponent } from '../employee/pages/employee-registration/employee-registration.component';
import { HomeComponent } from '../employee/pages/home/home.component';
import { LeadHomeComponent } from '../lead/pages/lead-home/lead-home.component';
import { NotFoundComponent } from '../shared/components/not-found/not-found.component';
import { SignUpComponent } from '../shared/components/sign-up/sign-up.component';

const routes: Routes = [
  {
    path: 'employee',
    component: HomeComponent,
  },
  {
    path: 'lead',
    component: LeadHomeComponent,
  },
  {
    path: 'sign-in',
    component: LoginComponent,
  },
  {
    path: 'sign-up',
    component: SignUpComponent,
  },
  { path: '', component: HomeComponent },
  { path: '**', component: NotFoundComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
