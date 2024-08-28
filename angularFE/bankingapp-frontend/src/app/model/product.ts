export interface Product {
  id?: string;
  productDefinitionKey: string;
  productType: string;
  rateType: string;
  rate: number;
}

export interface ProductResponse {
  products: Product[];
}