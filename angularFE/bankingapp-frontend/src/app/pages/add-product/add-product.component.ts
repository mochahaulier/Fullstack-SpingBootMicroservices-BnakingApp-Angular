import { Component, inject, signal } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Product} from "../../model/product";
import {ProductRequest} from "../../model/product-request";
import {ProductService} from "../../services/product/product.service";
import {NgIf} from "@angular/common";
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import { ProductDefinition } from '../../model/productdefinition';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Router } from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, 
    MatFormFieldModule, MatInputModule, MatSelectModule, MatCardModule, MatButtonModule],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent {  
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly router = inject(Router);
  isAuthenticated = false;

  addProductForm: FormGroup;
  private readonly productService = inject(ProductService);
  
  definitions: ProductDefinition[] = [];
  payRateUnits: string[] = ['DAY', 'MONTH'];
  
  private _snackBar = inject(MatSnackBar);

  constructor(private fb: FormBuilder) {
    this.addProductForm = this.fb.group({
      definition: [null, [Validators.required]],
      rate: ['', [Validators.required]],
      unit: [{ value: '', disabled: true }, [Validators.required]],
      value: [{ value: '', disabled: true }, [Validators.required]]   
    })
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        this.isAuthenticated = isAuthenticated;        
        this.loadProductDefinitions();              
        
        const definitionControl = this.addProductForm.get('definition');

        if (definitionControl) {
          definitionControl.valueChanges.subscribe((selectedDefinition: ProductDefinition) => {
            this.updateFormValues(selectedDefinition);
          });
        }
      }
    );
  }

  loadProductDefinitions(): void {
    this.productService.getProductDefinitions()
      .subscribe(definitions => {
        this.definitions = definitions;
        console.log("Product definitions loaded:", this.definitions);
      });
  }

  onSubmit(): void {
    if (this.addProductForm.valid) {
      const productDefinitionKey = this.addProductForm.get('definition')?.value?.productKey;
      const rate = this.addProductForm.get('rate')?.value;

      if (productDefinitionKey && rate) {
        const productRequest: ProductRequest = {
          productDefinitionKey: productDefinitionKey,
          adjustedRate: rate
        };

        this.productService.createProduct(productRequest).subscribe({          
          next: () => {
            this._snackBar.open("Product created.", "OK");
            this.addProductForm.reset();            
          },
          error: (error) => {
            console.error('Error creating product:', error);
          }
        });
      }
    } else {
      console.log('Form is not valid');
    }
  }

  updateFormValues(definition: ProductDefinition): void {
    if (definition) {
      this.addProductForm.patchValue({
        rate: definition.rate,
        unit: definition.payRate?.unit || '',
        value: definition.payRate?.value || ''
      });
    }
  }

  submitForm(): void {
    if (this.addProductForm.valid) {
      const formData = this.addProductForm.getRawValue(); 
      console.log("Form submitted with values:", formData);
    } else {
      console.log("Form is invalid!");
    }
  }

  get definition() {
    return this.addProductForm.get('definition');
  }

  get rate() {
    return this.addProductForm.get('rate');
  }

  get unit() {
    return this.addProductForm.get('unit');
  }

  get value() {
    return this.addProductForm.get('value');
  }

  goToHomePage() {
    this.router.navigateByUrl('/');
  }
}
