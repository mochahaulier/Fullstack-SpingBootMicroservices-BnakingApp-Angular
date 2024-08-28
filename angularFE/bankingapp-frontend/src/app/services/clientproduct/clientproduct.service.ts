import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from "@angular/common/http";
import { map, Observable } from 'rxjs';
import { AccountProductResponse, ClientProduct, ClientProductResponse, LoanProductResponse } from '../../model/clientproduct';
import { ClientProductRequest } from '../../model/clientproduct-request';
import { ClientResponse } from '../../model/client';

@Injectable({
  providedIn: 'root'
})
export class ClientProductService {

  private clientProductUrl = 'http://localhost:8180/api/v1/client-products';

  constructor(private httpClient: HttpClient) {
  }

  getClientProducts(): Observable<Array<ClientProduct>> {
    console.log("Getting all client products...");
    // return this.httpClient.get<Array<ClientProduct>>(this.clientProductUrl);
    return this.httpClient.get<ClientProductResponse>(this.clientProductUrl)
    .pipe(
      map((response: ClientProductResponse) => response.clientproducts)
    );
  }

  getClientProductsByClientId(clientId: string): Observable<Array<ClientProduct>> {
    console.log("Getting all client products for client: ", clientId);
    // return this.httpClient.get<Array<ClientProduct>>(`${this.clientProductUrl}/client/${clientId}`);
    return this.httpClient.get<ClientProductResponse>(`${this.clientProductUrl}/client/${clientId}`)
    .pipe(
      map((response: ClientProductResponse) => response.clientproducts)
    );
  }

  getAccountProductsByClientId(clientId: string): Observable<Array<ClientProduct>> {
    console.log("Getting all client products for client: ", clientId);
    // return this.httpClient.get<Array<ClientProduct>>(`${this.clientProductUrl}/client/${clientId}/accounts`);
    return this.httpClient.get<AccountProductResponse>(`${this.clientProductUrl}/client/${clientId}/accounts`)
    .pipe(
      map((response: AccountProductResponse) => response.accountproducts)
    );
  }

  getLoanProductsByClientId(clientId: string): Observable<Array<ClientProduct>> {
    console.log("Getting all client products for client: ", clientId);
    // return this.httpClient.get<Array<ClientProduct>>(`${this.clientProductUrl}/client/${clientId}/loans`);
    return this.httpClient.get<LoanProductResponse>(`${this.clientProductUrl}/client/${clientId}/loans`)
    .pipe(
      map((response: LoanProductResponse) => response.loanproducts)
    );
  }

  createClientProduct(clientproduct: ClientProductRequest): Observable<HttpResponse<void>> {
    console.log("Creating client product...");
    return this.httpClient.post<void>(this.clientProductUrl, clientproduct, { observe: 'response' });
  }
 
}
