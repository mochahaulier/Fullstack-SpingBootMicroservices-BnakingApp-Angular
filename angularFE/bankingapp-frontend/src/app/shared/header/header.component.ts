import {Component, inject, OnInit} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";

import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatChipsModule} from '@angular/material/chips';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [ MatToolbarModule, MatButtonModule, MatIconModule, MatChipsModule, MatMenuModule, MatDividerModule ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  isAuthenticated = false;
  username = "";

  private readonly router = inject(Router);

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({isAuthenticated}) => {
        this.isAuthenticated = isAuthenticated;
      }
    )
    this.oidcSecurityService.userData$.subscribe(
      ({userData}) => {
        this.username = userData.preferred_username
      }
    )
  }

  login(): void {
    this.oidcSecurityService.authorize();
  }

  logout(): void {
    this.oidcSecurityService
      .logoff()
      .subscribe((result) => console.log(result));
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
}
