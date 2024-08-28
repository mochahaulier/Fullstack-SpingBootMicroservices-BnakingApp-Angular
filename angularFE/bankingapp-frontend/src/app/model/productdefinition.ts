interface PayRate {
    unit: string;
    value: number;
}

export interface ProductDefinition {
    productKey: string;
    description: string;
    type: string;
    rate: number;
    payRate: PayRate;
    createdDate: string;
    modifiedDate: string;
}
  
export interface DefinitionResponse {
   definitions: ProductDefinition[];
}