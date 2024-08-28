import { Component, inject } from '@angular/core';
import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import {ClientProductService} from "../../services/clientproduct/clientproduct.service";
import { ClientProductRequest } from '../../model/clientproduct-request';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Router } from '@angular/router';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {NgIf} from "@angular/common";
import {MatDatepickerModule} from '@angular/material/datepicker';
import { ClientService } from '../../services/client/client.service';
import { ProductService } from '../../services/product/product.service';
import { Product } from '../../model/product';
import { Client } from '../../model/client';
import {MatNativeDateModule} from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-clientproduct',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, MatFormFieldModule, MatInputModule, MatSelectModule, 
    MatCardModule, MatButtonModule, MatDatepickerModule, MatNativeDateModule],
  templateUrl: './add-clientproduct.component.html',
  styleUrl: './add-clientproduct.component.css'
})
export class AddClientProductComponent {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly router = inject(Router);
  isAuthenticated = false;

  private readonly productService = inject(ProductService);
  private readonly clientService = inject(ClientService);
  
  products: Product[] = [];
  clients: Client[] = [];

  selectedClient: Client | undefined;
  selectedProduct: Product | undefined;

  addClientProductForm: FormGroup;
  private readonly clientProductService = inject(ClientProductService);
  
  private _snackBar = inject(MatSnackBar);

  constructor(private fb: FormBuilder) {
    this.addClientProductForm = this.fb.group({
      client: [null, Validators.required],
      product: [null, Validators.required],
      initialBalance: [''],
      loanAmount: [''],
      startDate: ['', Validators.required],
      endDate: [''],
      fixedInstallment: ['']
    })
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;                             
        
        this.loadClients();
        this.loadProducts();
        
        const clientControl = this.addClientProductForm.get('client');
        const productControl = this.addClientProductForm.get('product');

        if (clientControl) {
          clientControl.valueChanges.subscribe((client: Client) => {
            this.selectedClient = client;
          });
        }

        if (productControl) {
          productControl.valueChanges.subscribe((product: Product) => {
            this.selectedProduct = product;
            this.updateFormFields(product.productType);
          });
        }     
      }
    );
  }

  updateFormFields(productType: string | undefined): void {
    // Clear existing validators
    const initialBalanceControl = this.addClientProductForm.get('initialBalance');
    const loanAmountControl = this.addClientProductForm.get('loanAmount');
    const endDateControl = this.addClientProductForm.get('endDate');
    const fixedInstallmentControl = this.addClientProductForm.get('fixedInstallment');

    if (initialBalanceControl) initialBalanceControl.clearValidators();
    if (loanAmountControl) loanAmountControl.clearValidators();
    if (endDateControl) endDateControl.clearValidators();
    if (fixedInstallmentControl) fixedInstallmentControl.clearValidators();

    // Set validators based on the product type
    if (productType === 'ACCOUNT') {
      if (initialBalanceControl) initialBalanceControl.setValidators([Validators.required]);
    } else if (productType === 'LOAN') {
      if (loanAmountControl) loanAmountControl.setValidators([Validators.required]);
      if (endDateControl) endDateControl.setValidators([Validators.required]);
      if (fixedInstallmentControl) fixedInstallmentControl.setValidators([Validators.required]);
    }

    // Update the form validation state
    this.addClientProductForm.updateValueAndValidity();
  }

  onSubmit(): void {
    console.log('Submitting...');
    if (this.addClientProductForm.valid) {
      const clientId = this.addClientProductForm.get('client')?.value?.id;
      const productId = this.addClientProductForm.get('product')?.value?.id;

      console.log('IDs...', productId, clientId);

      if (clientId && productId) {
        console.log('Adding product to client!:', productId, clientId);
        const clientProduct: ClientProductRequest = {
          clientId: clientId,
          productId: productId,
          initialBalance: this.addClientProductForm.get('initialBalance')?.value,
          startDate: this.addClientProductForm.get('startDate')?.value,
          endDate: this.addClientProductForm.get('endDate')?.value,      
          loanAmount: this.addClientProductForm.get('loanAmount')?.value,
          fixedInstallment: this.addClientProductForm.get('fixedInstallment')?.value
        }
        this.clientProductService.createClientProduct(clientProduct).subscribe({
          next: () => {
            this._snackBar.open("Product added to client.", "OK");
            this.addClientProductForm.reset();            
          },
          error: (error) => {
            console.error('Error creating client product:', error);
          }
        });        
      } else {
        console.log('No valid IDs!');
      }
    } else {
      console.log('Form is not valid!');    }
    
  }

  loadClients(): void {
    this.clientService.getClients()
    .pipe()
    .subscribe(client => {
      this.clients = client;
      console.log("Clients loaded:", this.clients);
    });
  }

  loadProducts(): void {
    this.productService.getProducts()
          .pipe()
          .subscribe(product => {
            this.products = product;
            console.log("Products loaded:", this.products);
          });
  }

  goToHomePage() {
    this.router.navigateByUrl('/');
  }
}
