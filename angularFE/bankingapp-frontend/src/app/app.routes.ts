import {Routes} from '@angular/router';
import {HomePageComponent} from "./pages/home-page/home-page.component";
import {AddClientComponent} from "./pages/add-client/add-client.component";
import {AddProductComponent} from "./pages/add-product/add-product.component";
import {AddClientProductComponent} from "./pages/add-clientproduct/add-clientproduct.component";

export const routes: Routes = [
  {path: '', component: HomePageComponent},
  {path: 'add-client', component: AddClientComponent},
  {path: 'add-product', component: AddProductComponent},
  {path: 'add-clientproduct', component: AddClientProductComponent}
];
