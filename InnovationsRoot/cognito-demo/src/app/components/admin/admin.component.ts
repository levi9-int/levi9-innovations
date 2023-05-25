import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Product } from 'src/app/models/product';
import { User } from 'src/app/models/user';
import { CognitoService } from 'src/app/service/cognito.service';
import { ProductService } from 'src/app/service/product.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent {
  allProductsList: Product[] = []; 
  craetedProduct: Product = {} as Product;
  showAddProductsPopup: boolean = false;
  user: User = {} as User;

  constructor(private router: Router, private cognitoService: CognitoService, private productService: ProductService) {}

  ngOnInit(): void {
    this.getUserDetails();
  }

  private getUserDetails() {
    this.cognitoService.getUser()
    .then((user:any) => {
      if (user) {
        this.user.givenName = user.attributes.given_name;
        this.user.familyName = user.attributes.family_name;
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
        console.log("LISTAAA1111");
        this.allProductsList = res;
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

  showAddProductsForm() {
    this.showAddProductsPopup = true;
  }

  closeAddProductsForm() {
    this.showAddProductsPopup = false;
  }

  addProduct(){
    this.productService.addProduct(this.craetedProduct).subscribe({
      next: (res) => {
        this.fetchAllProducts();
        this.closeAddProductsForm();
      },
      error: (err) => {
        console.log(err);
      }
    })
  }
}
