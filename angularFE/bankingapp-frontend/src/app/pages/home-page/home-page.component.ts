import {AfterViewInit, Component, inject, OnInit, ViewChild} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";
import {ClientService} from "../../services/client/client.service";
import {TransactionService} from "../../services/transaction/transaction.service";
import {AsyncPipe, JsonPipe} from "@angular/common";
import {Router} from "@angular/router";
import { Client } from '../../model/client';
import { ClientProduct } from '../../model/clientproduct';
import { Transaction } from '../../model/transaction';
import {ClientProductService} from "../../services/clientproduct/clientproduct.service";

import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {FormControl, Validators, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule, SortDirection} from '@angular/material/sort';
import {MatTabChangeEvent, MatTabsModule} from '@angular/material/tabs';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

import {NgIf} from "@angular/common";

import { DbTableComponent } from '../../components/db-table/db-table.component';

@Component({
  selector: 'app-homepage',
  templateUrl: './home-page.component.html',
  standalone: true,
  imports: [
    AsyncPipe, NgIf,
    JsonPipe,
    FormsModule, MatProgressSpinnerModule,
    MatSlideToggleModule, MatIconModule, MatTableModule, MatSortModule, MatPaginatorModule, MatTabsModule,
    MatFormFieldModule, MatSelectModule, FormsModule, ReactiveFormsModule, MatInputModule, MatCardModule,
    DbTableComponent   
  ],
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit, AfterViewInit {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly transactionService = inject(TransactionService);
  private readonly clientService = inject(ClientService);
  private readonly clientProductService = inject(ClientProductService);
  private readonly router = inject(Router);
  isAuthenticated = false;

  clients: Client[] = [];
  clientproducts: Array<ClientProduct> = [];
  transactions: Transaction[] = [];
  accountproducts: ClientProduct[] = [];
  loanproducts: ClientProduct[] = [];
 
  clientControl = new FormControl<Client | null>(null, Validators.required);
  selectClientFormControl = new FormControl('', Validators.required);
  
  @ViewChild(MatSort) sortCP: MatSort | null = null;
  @ViewChild(MatPaginator) paginatorCP: MatPaginator | null = null;
  @ViewChild(MatSort) sortAP: MatSort | null = null;
  @ViewChild(MatPaginator) paginatorAP: MatPaginator | null = null;
  @ViewChild(MatSort) sortLP: MatSort | null = null;
  @ViewChild(MatPaginator) paginatorLP: MatPaginator | null = null;
  @ViewChild(MatSort) sortTR: MatSort | null = null;
  @ViewChild(MatPaginator) paginatorTR: MatPaginator | null = null;

  dataSourceClientProducts: MatTableDataSource<ClientProduct> = new MatTableDataSource<ClientProduct>([]);
  dataSourceAccountProducts: MatTableDataSource<ClientProduct> = new MatTableDataSource<ClientProduct>([]);
  dataSourceLoanProducts: MatTableDataSource<ClientProduct> = new MatTableDataSource<ClientProduct>([]);
  dataSourceTransactions: MatTableDataSource<Transaction> = new MatTableDataSource<Transaction>([]);

  isLoadingAccounts = false;
  isLoadingLoans = false;
  isLoadingTransactions = false;

  selectedClient: Client | null = null;
  activeTabIndex: number = 0;  // Track the active tab index

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;
               
        this.loadClients();
        
        this.clientControl.valueChanges.subscribe(client => {
          if (client?.id) {
            this.selectedClient = client;
            this.loadDataForActiveTab();  // Load data for the active tab
            // this.loadClientProducts(client.id);
            // this.loadAccountProducts(client.id);
            // this.loadLoanProducts(client.id);
            // this.loadTransactions(client.id);
          }
        });
      }
    );
  }

  onTabChange(event: MatTabChangeEvent) {
    this.activeTabIndex = event.index;  // Update the active tab index
    this.loadDataForActiveTab();  // Load data based on the current client and active tab
  }

  loadDataForActiveTab() {
    if (this.selectedClient?.id) {
      switch (this.activeTabIndex) {
        case 0:
          this.loadAccountProducts(this.selectedClient?.id);
          break;
        case 1:
          this.loadLoanProducts(this.selectedClient?.id);
          break;
        case 2:
          this.loadTransactions(this.selectedClient?.id);
          break;
      }
    }
  }

  loadClients(): void {
    this.clientService.getClients()
    .pipe()
    .subscribe(client => {
      this.clients = client;
      console.log("Clients loaded:", this.clients);
    });
  }

  loadClientProducts(clientId: string): void {
    this.clientProductService.getClientProductsByClientId(clientId)
          .pipe()
          .subscribe(clientproduct => {
            this.clientproducts = clientproduct;
            console.log("Clientproducts for client:", clientId);
            console.log("Clientproducts loaded:", this.clientproducts);
            this.dataSourceClientProducts = new MatTableDataSource(this.clientproducts);
            this.dataSourceClientProducts.sort = this.sortCP;
            this.dataSourceClientProducts.paginator = this.paginatorCP;            
          });
  }
  
  loadAccountProducts(clientId: string): void {
    this.isLoadingAccounts = true;
    this.clientProductService.getAccountProductsByClientId(clientId)
          .pipe()
          .subscribe(accountproduct => {
            this.accountproducts = accountproduct;
            console.log("Accountproducts for client:", clientId);
            console.log("Accountproducts loaded:", this.accountproducts);
            this.dataSourceAccountProducts = new MatTableDataSource(this.accountproducts);
            this.dataSourceAccountProducts.sort = this.sortAP;
            this.dataSourceAccountProducts.paginator = this.paginatorAP;
            this.isLoadingAccounts = false;
          });
  }

  loadLoanProducts(clientId: string): void {
    this.isLoadingLoans = true;
    this.clientProductService.getLoanProductsByClientId(clientId)
          .pipe()
          .subscribe(loanproduct => {
            this.loanproducts = loanproduct;
            console.log("Loanproducts for client:", clientId);
            console.log("Loanproducts loaded:", this.loanproducts);
            this.dataSourceLoanProducts = new MatTableDataSource(this.loanproducts);
            this.dataSourceLoanProducts.sort = this.sortLP;
            this.dataSourceLoanProducts.paginator = this.paginatorLP;
            this.isLoadingLoans = false;
          });
  }

  loadTransactions(clientId: string): void {
    this.isLoadingTransactions = true;
    this.transactionService.getTransactionsByClientId(clientId)
          .pipe()
          .subscribe(transaction => {
            this.transactions = transaction;
            console.log("transactions for client:", clientId);
            console.log("transactions loaded:", this.transactions);
            this.dataSourceTransactions = new MatTableDataSource(this.transactions);
            this.dataSourceTransactions.sort = this.sortTR;
            this.dataSourceTransactions.paginator = this.paginatorTR;
            this.isLoadingTransactions = false;
          });
  }

  ngAfterViewInit() {
    if (this.dataSourceClientProducts) {
      this.dataSourceClientProducts.sort = this.sortCP;
      this.dataSourceClientProducts.paginator = this.paginatorCP;
    }
    if (this.dataSourceAccountProducts) {
      this.dataSourceAccountProducts.sort = this.sortAP;
      this.dataSourceAccountProducts.paginator = this.paginatorAP;
    }
    if (this.dataSourceLoanProducts) {
      this.dataSourceLoanProducts.sort = this.sortLP;
      this.dataSourceLoanProducts.paginator = this.paginatorLP;
    }
    if (this.dataSourceTransactions) {
      this.dataSourceTransactions.sort = this.sortTR;
      this.dataSourceTransactions.paginator = this.paginatorTR;
    }
  }

  goToCreateProductPage() {
    this.router.navigateByUrl('/add-product');
  }

  goToCreateClientPage() {
    this.router.navigateByUrl('/add-client');
  }

  goToCreateClientProductPage() {
    this.router.navigateByUrl('/add-clientproduct');
  }

  columnsCP = [
    {
      columnDef: 'id',
      header: 'Id',
      cell: (element: ClientProduct) => `${element.id}`,
    },    
    {
      columnDef: 'product',
      header: 'ProductId',
      cell: (element: ClientProduct) => `${element.productId}`,
    },
    {
      columnDef: 'type',
      header: 'Type',
      cell: (element: ClientProduct) => `${element.productType}`,
    },
    {
      columnDef: 'date',
      header: 'LastCharged',
      cell: (element: ClientProduct) => `${element.lastChargeDate}`,
    },
  ];
  displayedColumnsCP = this.columnsCP.map(c => c.columnDef);

  columnsAP = [
    {
      columnDef: 'id',
      header: 'Id',
      cell: (element: ClientProduct) => `${element.id}`,
    },    
    {
      columnDef: 'product',
      header: 'ProductId',
      cell: (element: ClientProduct) => `${element.productId}`,
    },
    {
      columnDef: 'balance',
      header: 'Balance',
      cell: (element: ClientProduct) => `${element.accountBalance}`,
    },
    {
      columnDef: 'date',
      header: 'StartDate',
      cell: (element: ClientProduct) => `${element.startDate}`,
    },
  ];
  displayedColumnsAP = this.columnsAP.map(c => c.columnDef);

  columnsLP = [
    {
      columnDef: 'id',
      header: 'Id',
      cell: (element: ClientProduct) => `${element.id}`,
    },
    {
      columnDef: 'product',
      header: 'ProductId',
      cell: (element: ClientProduct) => `${element.productId}`,
    },
    {
      columnDef: 'loan',
      header: 'Loan',
      cell: (element: ClientProduct) => `${element.originalAmount}`,
    },
    {
      columnDef: 'installment',
      header: 'Installment',
      cell: (element: ClientProduct) => `${element.fixedInstallment}`,
    },    
  ];
  displayedColumnsLP = this.columnsLP.map(c => c.columnDef);

  columnsTR = [
    {
      columnDef: 'id',
      header: 'Id',
      cell: (element: Transaction) => `${element.id}`,
    },
    {
      columnDef: 'product',
      header: 'ProductId',
      cell: (element: Transaction) => `${element.clientProductId}`,
    },
    {
      columnDef: 'loan',
      header: 'Loan',
      cell: (element: Transaction) => `${element.transactionType}`,
    },
    {
      columnDef: 'installment',
      header: 'Installment',
      cell: (element: Transaction) => `${element.amount}`,
    },    
  ];
  displayedColumnsTR = this.columnsTR.map(c => c.columnDef);
}
