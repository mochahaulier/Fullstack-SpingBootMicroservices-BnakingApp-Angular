import { Injectable } from '@angular/core';
import { Transaction, TransactionResponse } from '../../model/transaction';
import { map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private transactionUrl = 'http://localhost:8180/api/v1/transaction';

  constructor(private httpClient: HttpClient) {
  }

  getTransactionsByClientId(clientId: string): Observable<Transaction[]> {
    console.log("Getting all transactions for client: ", clientId);
    // return this.httpClient.get<Array<Transaction>>(`${this.transactionUrl}/client/${clientId}`);
    return this.httpClient.get<TransactionResponse>(`${this.transactionUrl}/clients/${clientId}`)
    .pipe(
      map((response: TransactionResponse) => response.transactions)
    );
  }
 
}


  
