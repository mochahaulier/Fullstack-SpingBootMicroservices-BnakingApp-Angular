import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Client} from "../../model/client";
import {ClientService} from "../../services/client/client.service";
import {NgIf} from "@angular/common";
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-add-client',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf,
    MatFormFieldModule, MatInputModule, MatSelectModule, MatCardModule, MatButtonModule],
  templateUrl: './add-client.component.html',
  styleUrl: './add-client.component.css'
})
export class AddClientComponent {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly router = inject(Router);
  isAuthenticated = false;
  
  addClientForm: FormGroup;
  private readonly clientService = inject(ClientService);
  
  private _snackBar = inject(MatSnackBar);

  constructor(private fb: FormBuilder) {
    this.addClientForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required]],
      phone: ['', [Validators.required]]
    })
  }

  onSubmit(): void {
    if (this.addClientForm.valid) {
      const client: Client = {
        firstName: this.addClientForm.get('firstName')?.value,
        lastName: this.addClientForm.get('lastName')?.value,
        email: this.addClientForm.get('email')?.value,
        phone: this.addClientForm.get('phone')?.value
      }
      this.clientService.createClient(client).subscribe(client => {
        this._snackBar.open("Client created.", "OK");
        this.addClientForm.reset();
      })
    } else {
      console.log('Form is not valid');
    }
  }

  get firstName() {
    return this.addClientForm.get('firstName');
  }

  get lastName() {
    return this.addClientForm.get('lastName');
  }

  get email() {
    return this.addClientForm.get('email');
  }

  get phone() {
    return this.addClientForm.get('phone');
  }

  goToHomePage() {
    this.router.navigateByUrl('/');
  }
}
