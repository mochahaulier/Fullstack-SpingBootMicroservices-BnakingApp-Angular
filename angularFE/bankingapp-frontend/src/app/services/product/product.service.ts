import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {Product, ProductResponse} from "../../model/product";
import {ProductRequest} from "../../model/product-request";
import { DefinitionResponse, ProductDefinition } from '../../model/productdefinition';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private productUrl = 'http://localhost:8180/api/v1/products';
  private definitionUrl = 'http://localhost:8180/api/v1/product-definitions';

  constructor(private httpClient: HttpClient) {
  }

  getProducts(): Observable<Array<Product>> {
    console.log("Getting products...")
    // return this.httpClient.get<Array<Product>>(this.productUrl);
    return this.httpClient.get<ProductResponse>(this.productUrl)
    .pipe(
      map((response: ProductResponse) => response.products)
    );
  }
  
  createProduct(product: ProductRequest): Observable<HttpResponse<void>> {
    console.log("Posting product...")
    return this.httpClient.post<void>(this.productUrl, product, { observe: 'response' });
  }

  getProductDefinitions(): Observable<ProductDefinition[]> {
    console.log("Getting product definitions...");
    return this.httpClient.get<DefinitionResponse>(this.definitionUrl)
      .pipe(
        map((response: DefinitionResponse) => response.definitions)
      );
  }
}
