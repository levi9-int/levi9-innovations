import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment.development';
import { Product } from '../models/product';
import { Observable } from 'rxjs';
import { BoughtProduct } from '../models/BoughtProduct';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly API_PATH = environment.apiPath;

  constructor(private http: HttpClient) { }

  fetchAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.API_PATH + 'get-products');
  }

  fetchBoughtProducts(): Observable<BoughtProduct[]> {
    return this.http.get<BoughtProduct[]>(this.API_PATH + 'get-products?bought=true');
  }

  addProduct(product: Product): Observable<any> {
    return this.http.post<any>(this.API_PATH + 'add-products', product);
  }

  buyProduct(buyProductDto: any): Observable<any> {
    return this.http.post<any>(this.API_PATH + 'buy-product', buyProductDto);
  }
    
}
