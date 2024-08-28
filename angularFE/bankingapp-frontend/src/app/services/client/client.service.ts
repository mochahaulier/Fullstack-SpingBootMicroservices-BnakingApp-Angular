import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { map, Observable } from 'rxjs';
import { Client, ClientResponse } from '../../model/client';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private clientUrl = 'http://localhost:8180/api/v1/clients';

  constructor(private httpClient: HttpClient) {
  }

  getClients(): Observable<Client[]> {
    console.log("Getting clients...");
    // return this.httpClient.get<Array<Client>>(this.clientUrl);
    return this.httpClient.get<ClientResponse>(this.clientUrl)
    .pipe(
      map((response: ClientResponse) => response.clients)
    );
  }

  createClient(client: Client): Observable<Client> {
    return this.httpClient.post<Client>(this.clientUrl, client);
  }
}