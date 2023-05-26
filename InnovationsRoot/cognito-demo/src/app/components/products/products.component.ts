import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BoughtProduct } from 'src/app/models/BoughtProduct';
import { Product } from 'src/app/models/product';
import { CognitoService } from 'src/app/service/cognito.service';
import { EmployeeService } from 'src/app/service/employee.service';
import { ProductService } from 'src/app/service/product.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent {

  allProductsList: Product[] = []; 
  boughtProductsList: BoughtProduct[] = []; 
  showAllProducts: boolean = true;
  showBoughtProducts: boolean = false;
  userId: string = "";

  constructor(private router: Router, private cognitoService: CognitoService, private employeeService: EmployeeService, private productService: ProductService) {}

  ngOnInit(): void {
    this.getUserDetails();
    this.fetchAllProducts();
    // this.fetchBoughtInnovations();
  }

  private getUserDetails() {
    this.cognitoService.getUser()
    .then((user:any) => {
      if (user) {
        this.userId = user.username;
        this.fetchAllProducts();
      }
      else {
        this.router.navigate(['/sign-in']);
      }
    })
  }

  private fetchAllProducts() {
    this.productService.fetchAllProducts().subscribe({
      next: (res) => {
        this.allProductsList = res;
      },
      error: (err) => {
        console.log(err);
      }
    })
  }

  private fetchBoughtInnovations() {
    this.productService.fetchBoughtProducts().subscribe({
      next: (res) => {
        this.boughtProductsList = res;
      },
      error: (err) => {
        alert(err.error);
      }
    })
  }

  buyProduct(id: string) {
    const buyProdDto = {
      'employee': this.userId,
      'product': id
    }
    this.productService.buyProduct(buyProdDto).subscribe({
      next: (res) => {
        // this.boughtProductsList = res;
        alert('Purchase successfull!')
        this.fetchAllProducts();
      },
      error: (err) => {
        alert(err.error);
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

  toggleAllProductsForm() {
    this.showAllProducts = true;
    this.showBoughtProducts = false;
  }

  toggleBoughtProductsForm() {
    this.fetchBoughtInnovations();
    this.showAllProducts = false;
    this.showBoughtProducts = true;
  }


}
