import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from '../shared/components/login/login.component';
import { EmployeeRegistrationComponent } from '../employee/pages/employee-registration/employee-registration.component';
import { HomeComponent } from '../employee/pages/home/home.component';
import { LeadHomeComponent } from '../lead/pages/lead-home/lead-home.component';
import { NotFoundComponent } from '../shared/components/not-found/not-found.component';

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
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'registration',
    component: EmployeeRegistrationComponent,
  },
  { path: '', component: LoginComponent },
  { path: '**', component: NotFoundComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
