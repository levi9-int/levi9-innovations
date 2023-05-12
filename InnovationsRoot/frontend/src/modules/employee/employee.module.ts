import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmployeeRegistrationComponent } from './pages/employee-registration/employee-registration.component';
import { HomeComponent } from './pages/home/home.component';


@NgModule({
  declarations: [
    EmployeeRegistrationComponent,
    HomeComponent,
  ],
  imports: [
    CommonModule,
  ]
})
export class EmployeeModule { }
