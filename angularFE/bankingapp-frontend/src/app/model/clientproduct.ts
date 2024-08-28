export interface ClientProduct {
    id?: string;
    clientId: string;
    productId: string;
    productType: string;
    lastChargeDate: string;    
    accountBalance?: number;
    startDate?: string;
    originalAmount?: number;
    fixedInstallment?: number;
    endDate?: string;
}

export interface ClientProductResponse {
    clientproducts: ClientProduct[];
}

export interface AccountProductResponse {
    accountproducts: ClientProduct[];
}

export interface LoanProductResponse {
    loanproducts: ClientProduct[];
}